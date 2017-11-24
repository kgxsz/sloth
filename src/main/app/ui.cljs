(ns app.ui
  (:require [app.operations :as ops]
            [app.schema :as schema]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]
            [fulcro.client.core :as fc]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once User
  static om/IQuery
  (query
   [_]
   [:user/first-name
    :user/avatar-url])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [first-name avatar-url]}]
   {:user/first-name first-name
    :user/avatar-url avatar-url})

  Object
  (render
   [this]
   (let [{:keys [user/first-name user/avatar-url]} (om/props this)]
     (dom/div
      #js {:className "user"}
      (dom/img
       #js {:className "user__avatar"
            :alt "user-avatar"
            :src avatar-url})
      (dom/span
       #js {:className "user__first-name"}
       first-name)

      (dom/div
       #js {:className "user__divider"})))))

(def ui-user (om/factory User))


(defui ^:once Calendar
  static om/IQuery
  (query
   [_]
   [:calendar/title
    :calendar/subtitle])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [title subtitle]}]
   {:calendar/title title
    :calendar/subtitle subtitle})

  Object
  (render
   [this]
   (let [{:keys [calendar/title calendar/subtitle]} (om/props this)]
     (dom/div
      #js {:className "calendar"}
      (dom/div
       #js {:className "calendar__header"}
       (dom/span
        #js {:className "calendar__header__title"}
        title)
       (dom/span
        #js {:className "calendar__header__separator"}
        "—")
       (dom/span
        #js {:className "calendar__header__subtitle"}
        subtitle))

      (dom/div
       #js {:className "calendar__body"}
       (dom/div
        #js {:className "calendar__body__section-left"}
        (doall
         (for [day-label ["Mon" "Wed" "Fri" "Sun"]]
           (dom/div
            #js {:key day-label
                 :className "calendar__day-label-container"}
            (dom/span
             #js {:className "calendar__label"}
             day-label)))))

       (dom/div
        #js {:className "calendar__body__section-right"}
        (dom/div
         #js {:className "calendar__body__section-right__insulator"}
         (dom/div
          #js {:className "calendar__days"}
          (let [formatter (tf/formatter "EEEE do 'of' MMMM, Y")
                today (t/today)
                days (->> today
                          (iterate #(t/minus- % (t/days 1)))
                          (take (+ 357 (t/day-of-week today)))
                          (reverse))]
            (doall
             (for [day days]
               (dom/div
                #js {:key (tf/unparse (tf/formatters :basic-date) day)
                     :title (tf/unparse formatter day)
                     :className "calendar__days__day"})))))

         (dom/div
          #js {:className "calendar__month-labels"}
          (let [formatter (tf/formatter "MMM")
                today (t/today)
                next-sunday (t/plus- today (t/days (- 7 (t/day-of-week today))))
                sundays (->> next-sunday
                             (iterate #(t/minus- % (t/weeks 1)))
                             (take 52))]
            (doall
             (for [sunday sundays]
               (let [show? (< (t/day sunday) 8)]
                 (dom/div
                  #js {:key (tf/unparse (tf/formatters :basic-date) sunday)
                       :className "calendar__month-labels__month-label-container"}
                  (when show?
                    (dom/span
                     #js {:className "calendar__label calendar__label--vertical"}
                     (tf/unparse formatter sunday))))))))))))

      (dom/div
       #js {:className "calendar__footer"})))))


(tc/from-long (tc/to-long (t/today)))


(def ui-calendar (om/factory Calendar))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    {:user (om/get-query User)}
    {:calendar (om/get-query Calendar)}])
  static fc/InitialAppState
  (initial-state
   [_ _]
   {:user (fc/get-initial-state User {:first-name "Keigo"
                                      :avatar-url "images/avatar.jpg"})
    :calendar (fc/get-initial-state Calendar {:title "Some title"
                                              :subtitle "some subtitle"})})

  Object
  (render
   [this]
   (let [{:keys [ui/react-key user calendar]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (ui-user user)
       (ui-calendar calendar))))))
