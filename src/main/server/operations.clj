(ns server.operations)

(ns app.operations
  (:require
   [fulcro.server :as server :refer [defquery-root defquery-entity defmutation]]
   [taoensso.timbre :as timbre]))

;; TODO - use a real db
(def db (atom {:user/by-id {#uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                            {:user/id #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2"
                             :user/first-name "Keigo"
                             :user/avatar-url "images/avatar.jpg"}}
               :calendar/by-id {#uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                {:calendar/id #uuid "67eb2269-35f1-4477-8061-60193cd4709e"
                                 :calendar/title "Some title"
                                 :calendar/subtitle "some subtitle"
                                 :calendar/colour :green
                                 :calendar/days []}
                                #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                {:calendar/id #uuid "b7f261e6-02b3-4b84-ab73-932b9d33f159"
                                 :calendar/title "Some other title"
                                 :calendar/subtitle "some other subtitle"
                                 :calendar/colour :yellow
                                 :calendar/days []}
                                #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                {:calendar/id #uuid "1db32759-5fba-477f-bcf7-3b9f1831b7cf"
                                 :calendar/title "Another really long title"
                                 :calendar/subtitle "another really really really long subtitle"
                                 :calendar/colour :blue
                                 :calendar/days []}}}))

;; TODO - make sure you use the incoming query to select the keys you need
(defquery-root :server/user
  (value [{:keys [query]} params]
         (get (:user/by-id @db) #uuid "d1cce9c9-171c-4616-ad61-d2990150dae2")))

(defquery-root :server/calendars
  (value [{:keys [query]} params]
         (->> (:calendar/by-id @db)
              (vals)
              (vec))))
