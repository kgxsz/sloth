(ns app.operations
  (:require [clj-time.core :as t]
            [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]))

;; TODO - deploy the in memory setup to Heroku
;; TODO - setup the database as part of a system start up
;; TODO - deploy the postgres backed transactor setup to Heroku
;; TODO - figure out this set to vector business
;; TODO - get authentication in place

(def db-uri "datomic:mem://core")

(defn create-dummy-db
  []
  (let [schema [{:db/doc "A user's first name"
                 :db/ident :user/first-name
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A user's last name"
                 :db/ident :user/last-name
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A user's avatar url"
                 :db/ident :user/avatar-url
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A user's calendars"
                 :db/ident :user/calendars
                 :db/valueType :db.type/ref
                 :db/cardinality :db.cardinality/many
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A calendar's title"
                 :db/ident :calendar/title
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A calendar's subtitle"
                 :db/ident :calendar/subtitle
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A calendar's colour option"
                 :db/ident :calendar/colour-option
                 :db/valueType :db.type/keyword
                 :db/cardinality :db.cardinality/one
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}
                {:db/doc "A calendar's checked dates"
                 :db/ident :calendar/checked-dates
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/many
                 :db/id #db/id [:db.part/db]
                 :db.install/_attribute :db.part/db}]
        temp-ids (take 3 (repeatedly #(d/tempid :db.part/user)))
        entities [{:db/id (nth temp-ids 0)
                   :calendar/title "Some Title"
                   :calendar/subtitle "some subtitle"
                   :calendar/colour-option :a
                   :calendar/checked-dates #{"20180110" "20180114" "20180122"}}
                  {:db/id (nth temp-ids 1)
                   :calendar/title "Another Title"
                   :calendar/subtitle "some other subtitle"
                   :calendar/colour-option :b
                   :calendar/checked-dates #{"20180101" "20180114" "20180122"}}
                  {:db/id (nth temp-ids 2)
                   :user/first-name "Keigo"
                   :user/last-name "Suzukawa"
                   :user/avatar-url "images/avatar.jpg"
                   :user/calendars #{(nth temp-ids 0) (nth temp-ids 1)}}]]

    (d/delete-database db-uri)
    (d/create-database db-uri)
    @(d/transact (d/connect db-uri) schema)
    @(d/transact (d/connect db-uri) entities)))

(comment

  (create-dummy-db)

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

;; TODO - make this part of the system startup
(create-dummy-db)

(defn get-current-user-id []
  (->> (d/db (d/connect db-uri))
       (d/q '[:find ?e
              :where [?e
                      :user/first-name "Keigo"]])
       (ffirst)))

(defquery-root :current-user
  (value [{:keys [query]} params]
         (let [db (d/db (d/connect db-uri))
               current-user-id (get-current-user-id)]
           (d/pull db query current-user-id))))

(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [_]
          @(d/transact (d/connect db-uri)
                       [[:db/add id :calendar/checked-dates date]])
          {}))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [_]
          @(d/transact (d/connect db-uri)
                      [[:db/retract id :calendar/checked-dates date]])
          {}))


