(ns app.operations
  (:require [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]
            [taoensso.timbre :as log]))

(defn get-user-id [current-db first-name]
  (->> current-db
       (d/q `[:find ?e
              :where [?e
                      :user/first-name ~first-name]])
       (ffirst)))

(defquery-root :user
  (value [{:keys [config db query]} {:keys [first-name]}]
         (let [{:keys [conn]} db
               current-db (d/db conn)
               user-id (get-user-id current-db first-name)]
           (d/pull current-db query user-id))))

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
