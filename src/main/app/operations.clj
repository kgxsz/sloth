(ns app.operations
  (:require [clj-time.core :as t]
            [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]
            [om.next :as om]))

(defn create-dummy-db
  []
  (let [db-uri "datomic:mem://core"
        delete-db-result (d/delete-database db-uri)
        create-db-result (d/create-database db-uri)
        conn (d/connect db-uri)
        schema [{:db/doc "A user's first name"
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
        schema-tx-result @(d/transact (d/connect db-uri) schema)
        temp-calendar-id (d/tempid :db.part/user)
        temp-user-id (d/tempid :db.part/user)
        entities [{:db/id temp-calendar-id
                   :calendar/title "Some Title"
                   :calendar/subtitle "some subtitle"
                   :calendar/colour-option :a
                   :calendar/checked-dates #{"20171125" "20171126" "20171127" "20171128"}}
                  {:db/id temp-user-id
                   :user/first-name "Keigo"
                   :user/last-name "Suzukawa"
                   :user/avatar-url "images/avatar.jpg"
                   :user/calendars [temp-calendar-id]}]
        {:keys [db-after tempids]} @(d/transact conn entities)]

    {:conn conn
     :calendar-id (d/resolve-tempid db-after tempids temp-calendar-id)
     :user-id (d/resolve-tempid db-after tempids temp-user-id)}))

(comment

  (def dummy-db (create-dummy-db))

  (d/touch (d/entity (d/db (:conn dummy-db)) (:calendar-id dummy-db)))

  (d/q '[:find ?e
         :where [?e :user/first-name "Jacob"]]
       (d/db (:conn dummy-db)))

  (d/pull (d/db (:conn dummy-db))
          [:db/id
           :user/first-name
           :user/last-name
           :user/avatar-url
           {:user/calendars [:db/id
                             :calendar/title
                             :calendar/subtitle
                             :calendar/colour-option
                             :calendar/checked-dates]}]
          (:user-id dummy-db))

  )

;; TODO - use sessions to avoid this
(def current-user-id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2")

;; TODO - use a real db
(def db (atom {:user/by-id {#uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                            {:db/id 17592186045418
                             :user/first-name "Keigo"
                             :user/last-name "Suzukawa"
                             :user/avatar-url "images/avatar.jpg"
                             :user/calendars [[:calendar/by-id 17592186045419]]}}
               :calendar/by-id {17592186045419
                                {:db/id 17592186045419
                                 :calendar/title "Cardio Training"
                                 :calendar/subtitle "freeletics or running"
                                 :calendar/colour-option :a
                                 :calendar/checked-dates #{"20171125" "20171126" "20171127" "20171128"}}}}))

(om.next/db->tree [:db/id
                   :user/first-name
                   :user/last-name
                   :user/avatar-url
                   {:user/calendars [:db/id
                                     :calendar/title
                                     :calendar/subtitle
                                     :calendar/colour-option
                                     :calendar/checked-dates]}]
                  (get (:user/by-id @db) current-user-id)
                  @db)

(defquery-root :current-user
  (value [{:keys [query]} params]
         (let [db @db]
           (om.next/db->tree query (get (:user/by-id db) current-user-id) db))))

;; TODO - get some real mutations going on
#_(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] conj date)))

;; TODO - get some real mutations going on
#_(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] disj date)))


