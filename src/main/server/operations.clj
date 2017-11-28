(ns server.operations
  (:require [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]))

;; TODO - shoudln't do this
(def today (t/today))

;; TODO - use a real db, not this nested bullshit
(def db (atom {:user/by-id {#uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                            #:user{:id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                                   :first-name "Keigo"
                                   :avatar-url "images/avatar.jpg"}}
               :calendar/by-id {#uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                #:calendar{:id #uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                           :title "Some title"
                                           :subtitle "some subtitle"
                                           :colour :green
                                           ;; TODO - deal with the latest day issue
                                           :days (into []
                                                       (for [date (->> today
                                                                       (iterate #(t/minus- % (t/days 1)))
                                                                       (take (+ 357 (t/day-of-week today)))
                                                                       (map tc/to-date)
                                                                       (reverse))]
                                                         #:day{:id (java.util.UUID/randomUUID)
                                                               :date date
                                                               :checked? (rand-nth [true false false false false])
                                                               :colour :green})) }
                                #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                #:calendar{:id #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                           :title "Some other title"
                                           :subtitle "some other subtitle"
                                           :colour :yellow
                                           :days (into []
                                                       (for [date (->> today
                                                                       (iterate #(t/minus- % (t/days 1)))
                                                                       (take (+ 357 (t/day-of-week today)))
                                                                       (map tc/to-date)
                                                                       (reverse))]
                                                         #:day{:id (java.util.UUID/randomUUID)
                                                               :date date
                                                               :checked? (rand-nth [true false false false false])
                                                               :colour :yellow}))}
                                #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                #:calendar{:id #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                           :title "Another really long title"
                                           :subtitle "another really really really long subtitle"
                                           :colour :blue
                                           :days (into []
                                                       (for [date (->> today
                                                                       (iterate #(t/minus- % (t/days 1)))
                                                                       (take (+ 357 (t/day-of-week today)))
                                                                       (map tc/to-date)
                                                                       (reverse))]
                                                         #:day{:id (java.util.UUID/randomUUID)
                                                               :date date
                                                               :checked? (rand-nth [true false false false false])
                                                               :colour :blue}))}}}))

;; TODO - make sure you use the incoming query to select the keys you need
(defquery-root :server/user
  (value [{:keys [query]} params]
         (get (:user/by-id @db) #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2")))

(defquery-root :server/calendars
  (value [{:keys [query]} params]
         (->> (:calendar/by-id @db)
              (vals)
              (vec))))

(defquery-root :server/days
  (value [{:keys [query]} params]
         (->> (:day/by-id @db)
              (vals)
              (vec))))
