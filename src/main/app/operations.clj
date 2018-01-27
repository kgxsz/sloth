(ns app.operations
  (:require [clj-time.core :as t]
            [datomic.api :as d]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]
            [om.next :as om]))

;; TODO - somehow shoehorn this db stuff somewhere
(comment
  ;; make a db-uri when running transactor
  (def db-uri "datomic:dev://localhost:4334/core")

  ;; make a db-uri when not running a transactor
  (def db-uri "datomic:mem://core")

  ;; create the database
  (d/create-database db-uri)

  ;; or delete the database
  (d/delete-database db-uri)

  ;; create the connection to the db
  (def conn (d/connect db-uri))

  ;; create the schemas
  (def user-schema [{:db/doc "A user's first name"
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
                     :db.install/_attribute :db.part/db}])

  (def calendar-schema [{:db/doc "A calendar's title"
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
                         :db.install/_attribute :db.part/db}])

  ;; add the schemas
  @(d/transact conn user-schema)

  @(d/transact conn calendar-schema)

  ;; create a calendars transaction
  (def temp-calendar-id (d/tempid :db.part/user))

  (def calendars-tx [{:db/id temp-calendar-id
                      :calendar/title "Some Title"
                      :calendar/subtitle "some subtitle"}])

  ;; apply the calendars transaction
  (def tx-result @(d/transact conn calendars-tx))

  ;; resolve the temp id
  (def calendar-id (d/resolve-tempid (:db-after tx-result) (:tempids tx-result) temp-calendar-id))

  ;; check the entity
  (d/touch (d/entity (d/db conn) calendar-id))

  ;; create a users transaction
  (def temp-user-id (d/tempid :db.part/user))

  (def users-tx [{:db/id temp-user-id
                  :user/first-name "Jacob"
                  :user/last-name "Suzukawa"
                  :user/avatar-url "images/avatar.jpg"
                  :user/calendars #{17592186045419}}])

  ;; apply the users transaction
  (def tx-result @(d/transact conn users-tx))

  ;; resolve the temp id
  (def user-id (d/resolve-tempid (:db-after tx-result) (:tempids tx-result) temp-user-id))

  ;; check the entity
  (d/touch (d/entity (d/db conn) user-id))

  ;; pull the user and the calendars
  (d/pull (d/db conn)
          [:user/first-name
           :user/last-name
           :user/avatar-url
           {:user/calendars [:calendar/title]}]
          user-id)


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


