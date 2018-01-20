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

  ;; create the connection to the db
  (def conn (d/connect db-uri))

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

(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] conj date)))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] disj date)))


