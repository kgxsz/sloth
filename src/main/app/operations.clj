(ns app.operations
  (:require [clj-time.core :as t]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
            [fulcro.client.impl.application :as app]
            [om.next :as om]))

;; TODO - use sessions to avoid this
(def current-user-id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2")

;; TODO - use a real db
(def db (atom {:user/by-id {#uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                            #:user{:id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                                   :names {:first-name "Keigo" :last-name "Suzukawa"}
                                   :avatar {:url "images/avatar.jpg"}
                                   :calendars [[:calendar/by-id #uuid "67eb2269-35f1-4477-8061-60193cd4709e"]
                                               [:calendar/by-id #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"]
                                               [:calendar/by-id #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"]]}}
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



