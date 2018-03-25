(ns app.operations
  (:require [clj-time.coerce :as time.coerce]
            [clj-time.core :as time]
            [datomic.api :as datomic]
            [fulcro.server :refer [defquery-root defmutation]]
            [taoensso.timbre :as log]))


(defn get-user-id [current-db first-name]
  (->> current-db
       (datomic/q `[:find ?e
                    :where [?e
                            :user/first-name ~first-name]])
       (ffirst)))


(defquery-root :user
  (value [{:keys [config db query]} {:keys [user-id]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)]
           (datomic/pull current-db query (Long/parseLong user-id)))))


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
  (value [{:keys [config db query]} {:keys [state]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)
               auth-attempt-id (Long/parseLong state)
               auth-attempt (datomic/pull current-db query auth-attempt-id)
               ;; TODO - do some real authentication flow here to get the user-id and to set the session
               user-id (get-user-id current-db "Keigo")]
           (assoc auth-attempt
                  :user-id user-id))))


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
