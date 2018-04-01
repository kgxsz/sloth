 (ns app.operations
  (:require [clj-time.coerce :as time.coerce]
            [clj-time.core :as time]
            [clojure.data.json :as json]
            [datomic.api :as datomic]
            [fulcro.server :refer [defquery-root defmutation augment-response]]
            [org.httpkit.client :as http]
            [camel-snake-kebab.core :as camel-snake-kebab]
            [taoensso.timbre :as log]))


(defquery-root :session-user
  (value [{:keys [config db query session] :as env} {:keys [user-id]}]
         (let [{:keys [db/id] :as session-user} (datomic/pull (datomic/db (:conn db)) query (:user-id session))]
           (when (some? id) session-user))))


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

                       (if-let [user-id (ffirst (datomic/q `[:find ?e :where [?e :user/facebook-id ~facebook-id]] current-db))]
                         (do
                           (log/infof "updating existing user for auth attempt %s, with facebook-id %s" auth-attempt-id facebook-id)
                           (let [data [[:db/add user-id :user/first-name first-name]
                                       [:db/add user-id :user/last-name last-name]
                                       [:db/add user-id :user/avatar-url (get-in picture [:data :url])]
                                       [:db/add user-id :user/auth-attempts auth-attempt-id]
                                       [:db/add auth-attempt-id :auth-attempt/succeeded-at (time.coerce/to-date (time/now))]]
                                 {:keys [db-after]} @(datomic/transact conn data)]
                                (augment-response
                                 (datomic/pull db-after query auth-attempt-id)
                                 #(assoc-in % [:session :user-id] user-id))))
                         (do
                           (log/infof "creating new user for auth attempt %s, with facebook-id %s" auth-attempt-id facebook-id)
                           (let [data [[:db/add "user-id" :user/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "user-id" :user/facebook-id facebook-id]
                                       [:db/add "user-id" :user/first-name first-name]
                                       [:db/add "user-id" :user/last-name last-name]
                                       [:db/add "user-id" :user/roles :user.roles/admin]
                                       [:db/add "user-id" :user/avatar-url (get-in picture [:data :url])]
                                       [:db/add "user-id" :user/auth-attempts auth-attempt-id]
                                       [:db/add "user-id" :user/calendars "calendar-id-a"]
                                       [:db/add "user-id" :user/calendars "calendar-id-b"]
                                       [:db/add "user-id" :user/calendars "calendar-id-c"]
                                       [:db/add "user-id" :user/calendars "calendar-id-d"]
                                       [:db/add "user-id" :user/calendars "calendar-id-e"]
                                       [:db/add "calendar-id-a" :calendar/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "calendar-id-a" :calendar/title "Exercise"]
                                       [:db/add "calendar-id-a" :calendar/subtitle "weights or cardio, at least half an hour"]
                                       [:db/add "calendar-id-a" :calendar/colour "#8ACA55"]
                                       [:db/add "calendar-id-b" :calendar/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "calendar-id-b" :calendar/title "Meditation"]
                                       [:db/add "calendar-id-b" :calendar/subtitle "at least ten minutes"]
                                       [:db/add "calendar-id-b" :calendar/colour "#FADA6E"]
                                       [:db/add "calendar-id-c" :calendar/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "calendar-id-c" :calendar/title "Coding"]
                                       [:db/add "calendar-id-c" :calendar/subtitle "at least one commit"]
                                       [:db/add "calendar-id-c" :calendar/colour "#D4A3E3"]
                                       [:db/add "calendar-id-d" :calendar/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "calendar-id-d" :calendar/title "Journaling"]
                                       [:db/add "calendar-id-d" :calendar/subtitle "writing or drawing, at least one entry"]
                                       [:db/add "calendar-id-d" :calendar/colour "#58A1F5"]
                                       [:db/add "calendar-id-e" :calendar/created-at (time.coerce/to-date (time/now))]
                                       [:db/add "calendar-id-e" :calendar/title "Reading"]
                                       [:db/add "calendar-id-e" :calendar/subtitle "Leisure or technical, at least a few pages"]
                                       [:db/add "calendar-id-e" :calendar/colour "#EB5468"]
                                       [:db/add auth-attempt-id :auth-attempt/succeeded-at (time.coerce/to-date (time/now))]]
                                 {:keys [db-after tempids]} @(datomic/transact conn data)]
                             (augment-response
                              (datomic/pull db-after query auth-attempt-id)
                              #(assoc-in % [:session :user-id] (get tempids "user-id")))))))

                     (do
                       (log/infof "api request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                       (let [data [[:db/add auth-attempt-id :auth-attempt/failed-at (time.coerce/to-date (time/now))]]
                             {:keys [db-after]} @(datomic/transact conn data)]
                         (datomic/pull db-after query auth-attempt-id)))))

                 (do
                   (log/infof "access token request failed for auth attempt %s, with status %s, and error %s " auth-attempt-id status body)
                   (let [data [[:db/add auth-attempt-id :auth-attempt/failed-at (time.coerce/to-date (time/now))]]
                         {:keys [db-after]} @(datomic/transact conn data)]
                     (datomic/pull db-after query auth-attempt-id)))))

             (do
               (log/info "unable to match auth attempt" auth-attempt-id)
               (assoc auth-attempt
                      :auth-attempt/failed-at (time.coerce/to-date (time/now))))))))


(defmutation add-checked-date!
  [{:keys [calendar-id date]}]
  (action [{:keys [config db session]}]
          (let [conn (:conn db)
                {:keys [user/calendars]} (datomic/pull (datomic/db conn) [:user/calendars] (:user-id session))]
           (when (some #{calendar-id} (map :db/id calendars))
             @(datomic/transact conn
                                [[:db/add calendar-id :calendar/checked-dates date]])))
          {}))


(defmutation remove-checked-date!
  [{:keys [calendar-id date]}]
  (action [{:keys [config db session]}]
          (let [conn (:conn db)
                {:keys [user/calendars]} (datomic/pull (datomic/db conn) [:user/calendars] (:user-id session))]
            (when (some #{calendar-id} (map :db/id calendars))
              @(datomic/transact (:conn db)
                                 [[:db/retract calendar-id :calendar/checked-dates date]])))
          {}))
