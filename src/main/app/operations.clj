(ns app.operations
  (:require [clj-time.core :as t]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]))

;; TODO - use a real db
(def db (atom {:user/by-id {#uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                            #:user{:id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                                   :first-name "Keigo"
                                   :avatar-url "images/avatar.jpg"}}
               :calendar/by-id {#uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                #:calendar{:id #uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                           :title "Some title"
                                           :subtitle "some subtitle"
                                           :colour :green
                                           :checked-dates #{"20171125"
                                                            "20171126"
                                                            "20171127"
                                                            "20171128"}}
                                #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                #:calendar{:id #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                           :title "Some other title"
                                           :subtitle "some other subtitle"
                                           :colour :yellow
                                           :checked-dates #{"20171125"
                                                            "20171126"
                                                            "20171127"
                                                            "20171128"}}
                                #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                #:calendar{:id #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                           :title "Another really long title"
                                           :subtitle "another really really really long subtitle"
                                           :colour :blue
                                           :checked-dates #{"20171008"
                                                            "20171101"
                                                            "20171122"
                                                            "20171128"}}}}))

;; TODO - make sure you use the incoming query to select the keys you need
(defquery-root :server/user
  (value [{:keys [query]} params]
         (get (:user/by-id @db) #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2")))

(defquery-root :server/calendars
  (value [{:keys [query]} params]
         (->> (:calendar/by-id @db)
              (vals)
              (vec))))

(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] conj date)))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [_]
          (swap! db update-in [:calendar/by-id id :calendar/checked-dates] disj date)))



