(ns app.operations
  (:require [clj-time.coerce :as time.coerce]
            [clj-time.core :as time]
            [clojure.data.json :as json]
            [datomic.api :as datomic]
            [fulcro.server :refer [defquery-root defmutation]]
            [org.httpkit.client :as http]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [taoensso.timbre :as log]))


;; TODO - rename this
(defn get-user-id [current-db facebook-id]
  (->> current-db
       (datomic/q `[:find ?e
                    :where [?e
                            :user/facebook-id ~facebook-id]])
       (ffirst)))


(defquery-root :user
  (value [{:keys [config db query]} {:keys [user-id]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)]
           ;; TODO - check if user exists first
           (datomic/pull current-db query user-id))))


(defquery-root :initialised-auth-attempt
  (value [{:keys [config db query]} _]
         (let [auth-attempt {:db/id "auth-attempt-id"
                             :auth-attempt/initialised-at (time.coerce/to-date (time/now))
                             :auth-attempt/client-id (get-in config [:value :auth :client-id])
                             :auth-attempt/redirect-url (get-in config [:value :auth :redirect-url])
                             :auth-attempt/scope (get-in config [:value :auth :scope])}
               {:keys [tempids db-after]} @(datomic/transact (:conn db) [auth-attempt])]
           (datomic/pull db-after query (get tempids "auth-attempt-id")))))


(defquery-root :finalised-auth-attempt
  (value [{:keys [config db query]} {:keys [code auth-attempt-id]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)
               ;; {:keys [sessions]} state
               auth-attempt (datomic/pull current-db query auth-attempt-id)
               {:auth-attempt/keys [initialised-at succeeded-at failed-at]} auth-attempt]

           (if initialised-at
             (let [{:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.12/oauth/access_token"
                                                               :method :get
                                                               :headers {"Accept" "application/json"}
                                                               :query-params {"client_id" (:auth-attempt/client-id auth-attempt)
                                                                              "client_secret" (get-in config [:value :auth :client-secret])
                                                                              "redirect_uri" (:auth-attempt/redirect-url auth-attempt)
                                                                              "code" code}
                                                               :timeout 7000})]

               (log/info "making access token request from Facebook for auth attempt" auth-attempt-id)

               (if (= 200 status)
                 (let [{:keys [access-token]} (json/read-str body :key-fn camel-snake-kebab/->kebab-case-keyword)
                       {:keys [status body error]} @(http/request {:url "https://graph.facebook.com/v2.12/me"
                                                                   :method :get
                                                                   :oauth-token access-token
                                                                   :headers {"Accept" "application/json"}
                                                                   :query-params {"fields" "first_name,last_name,picture.width(256).height(256)"}
                                                                   :timeout 7000})]

                   (log/info "making API request from Facebook for auth attempt" auth-attempt-id)

                   (if (= 200 status)

                     (let [{:keys [picture first-name last-name] facebook-id :id} (json/read-str body :key-fn camel-snake-kebab/->kebab-case-keyword)]

                       (log/infof "auth attempt %s succeeded for facebook-id %s" auth-attempt-id facebook-id)

                       (if-let [user-id (get-user-id current-db facebook-id)]
                         (do
                           (log/infof "updating existing user for auth attempt %s, with facebook-id %s" auth-attempt-id facebook-id)
                           @(datomic/transact conn [[:db/add user-id :user/first-name first-name]
                                                    [:db/add user-id :user/last-name last-name]
                                                    [:db/add user-id :user/avatar-url (get-in picture [:data :url])]
                                                    [:db/add user-id :user/auth-attempts auth-attempt-id]
                                                    [:db/add auth-attempt-id :auth-attempt/owner user-id]
                                                    [:db/add auth-attempt-id :auth-attempt/succeeded-at (time.coerce/to-date (time/now))]])
                           (datomic/pull (datomic/db conn) query auth-attempt-id))
                         (do
                           (log/infof "creating new user for auth attempt %s, with facebook-id %s" auth-attempt-id facebook-id)
                           @(datomic/transact conn [[:db/add #db/id [:db.part/user -1] :user/created-at (time.coerce/to-date (time/now))]
                                                    [:db/add #db/id [:db.part/user -1] :user/facebook-id facebook-id]
                                                    [:db/add #db/id [:db.part/user -1] :user/first-name first-name]
                                                    [:db/add #db/id [:db.part/user -1] :user/last-name last-name]
                                                    [:db/add #db/id [:db.part/user -1] :user/avatar-url (get-in picture [:data :url])]
                                                    [:db/add #db/id [:db.part/user -1] :user/auth-attempts auth-attempt-id]
                                                    [:db/add auth-attempt-id :auth-attempt/owner #db/id [:db.part/user -1]]
                                                    [:db/add auth-attempt-id :auth-attempt/succeeded-at (time.coerce/to-date (time/now))]])
                           (datomic/pull (datomic/db conn) query auth-attempt-id)))

                       ;; TODO - get stuff into the session store here
                       #_(swap! sessions assoc (:session-key request) {:user-id user-id}))

                     (do
                       (log/infof "api request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                       @(datomic/transact conn [[:db/add auth-attempt-id :auth-attempt/failed-at (time.coerce/to-date (time/now))]])
                       (datomic/pull (datomic/db conn) query auth-attempt-id))))

                 (do
                   (log/infof "access token request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                   @(datomic/transact conn [[:db/add auth-attempt-id :auth-attempt/failed-at (time.coerce/to-date (time/now))]])
                   (datomic/pull (datomic/db conn) query auth-attempt-id))))

             (do
               (log/info "unable to match auth attempt" auth-attempt-id)
               (assoc auth-attempt
                      :auth-attempt/failed-at (time.coerce/to-date (time/now))))))))


(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [{:keys [config db]}]
          @(datomic/transact (:conn db)
                             [[:db/add id :calendar/checked-dates date]])
          {}))


(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [{:keys [config db]}]
          @(datomic/transact (:conn db)
                             [[:db/retract id :calendar/checked-dates date]])
          {}))
