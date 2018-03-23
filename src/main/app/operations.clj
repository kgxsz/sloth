(ns app.operations
  (:require [datomic.api :as datomic]
            [fulcro.server :refer [defquery-root defmutation]]
            [clj-time.coerce :as time.coerce]
            [clj-time.core :as time]))


(defn get-user-id [current-db first-name]
  (->> current-db
       (datomic/q `[:find ?e
                    :where [?e
                            :user/first-name ~first-name]])
       (ffirst)))


(defquery-root :user
  (value [{:keys [config db query]} {:keys [first-name]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)
               user-id (get-user-id current-db first-name)]
           (datomic/pull current-db query user-id))))


(defquery-root :auth-attempt
  (value [{:keys [config db query]} {:keys [id]}]
         (let [{:keys [conn]} db
               current-db (datomic/db conn)]
           (datomic/pull current-db query id))))


(defmutation initialise-auth-attempt!
  [{:keys [tempid]}]
  (action [{:keys [config db]}]
          (let [auth-attempt {:db/id "auth-attempt-id"
                              :auth-attempt/initialised-at (time.coerce/to-date (time/now))
                              :auth-attempt/client-id (get-in config [:value :auth :client-id])
                              :auth-attempt/redirect-url (get-in config [:value :auth :redirect-url])
                              :auth-attempt/scope (get-in config [:value :auth :scope])}
                {:keys [tempids]} @(datomic/transact (:conn db) [auth-attempt])]
            {:fulcro.client.primitives/tempids {tempid (get tempids "auth-attempt-id")}})))


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
