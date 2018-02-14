(ns app.operations
  (:require [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]
            [taoensso.timbre :as log]))

(defn get-current-user-id [current-db]
  (->> current-db
       (d/q '[:find ?e
              :where [?e
                      :user/first-name "Keigo"]])
       (ffirst)))

(defquery-root :current-user
  (value [{:keys [config db query]} params]
         (let [{:keys [conn]} db
               current-db (d/db conn)
               current-user-id (get-current-user-id current-db)]
           (d/pull current-db query current-user-id))))

(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [{:keys [config db]}]
          @(d/transact (:conn db)
                       [[:db/add id :calendar/checked-dates date]])
          {}))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [{:keys [config db]}]
          @(d/transact (:conn db)
                      [[:db/retract id :calendar/checked-dates date]])
          {}))


