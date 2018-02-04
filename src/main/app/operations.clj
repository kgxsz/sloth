(ns app.operations
  (:require [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]))

;; TODO - get persistence locally
;; TODO - get persistence remotely
;; TODO - get a console up and running
;; TODO - document local development setup
;; TODO - figure out this set to vector business
;; TODO - get authentication in place
;; TODO - fix day label bug

#_(comment

  (def db-uri "datomic:sql://core?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")

  (d/create-database db-uri)
  (d/delete-database db-uri)

  (def conn (d/connect db-uri))

  (let [migrations [:sloth/user-schema
                    :sloth/calendar-schema
                    :sloth/entities]]
    (io.rkn.conformity/ensure-conforms conn (io.rkn.conformity/read-resource "migrations.edn") migrations))

  (d/q '[:find ?e
         :where [?e :user/first-name "Keigo"]]
       (d/db (d/connect db-uri)))

  (d/q '[:find ?e
         :where [?e :calendar/title "Some Title"]]
       (d/db (d/connect db-uri)))

  (d/touch (d/entity (d/db (d/connect db-uri)) 17592186045419))

  (d/pull (d/db (d/connect db-uri))
          [:db/id
           :user/first-name
           :user/last-name
           :user/avatar-url
           {:user/calendars [:db/id
                             :calendar/title
                             :calendar/subtitle
                             :calendar/colour-option
                             :calendar/checked-dates]}]
          17592186045420)

  )

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


