(ns app.operations
  (:require [datomic.api :as datomic]
            [fulcro.server :refer [defquery-root defmutation]]))


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
