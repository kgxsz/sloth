(ns app.ui
  (:require [app.operations :as ops]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]
            [cljs-time.predicates :as tp]
            [cljs-time.periodic :as tpc]
            [fulcro.client.core :as fc]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

;; TODO - move these into a utils file
(def title-formatter (tf/formatter "EEEE do 'of' MMMM, Y"))
(def month-label-formatter (tf/formatter "MMM"))
(def day-label-formatter (tf/formatter "E"))
(def basic-date-formatter (tf/formatters :basic-date))

;; TODO - find a better way to do this
(def days (let [today (t/today)]
            (tpc/periodic-seq (t/minus- today (t/days (+ 356 (t/day-of-week today))))
                              (t/plus- today (t/days 1))
                              (t/days 1))))

(defui ^:once Calendar
  static om/Ident
  (ident [_ props] [:calendar/by-id (:calendar/id props)])

  static om/IQuery
  (query
   [_]
   [:calendar/id
    :calendar/title
    :calendar/subtitle
    :calendar/colour
    :calendar/checked-dates])

  Object
  (render
   [this]
   (let [{:calendar/keys [id title subtitle colour checked-dates]} (om/props this)]
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
        #js {:className "calendar__days"}
        (doall
         (for [day days]
           (let [date (tf/unparse basic-date-formatter day)
                 checked? (contains? checked-dates date)]
             (dom/div
              #js {:key date
                   :title (tf/unparse title-formatter day)
                   ;; TODO - time to use utils for BEM
                   :className (str "calendar__days__day "
                                   "calendar__days__day--"
                                   (cond
                                     checked? (name colour)
                                     (odd? (t/month day)) "grey-medium"
                                     :else "grey-light"))
                   :onClick #(if checked?
                               (om/transact! this `[(ops/remove-checked-date! {:id ~id :date ~date})])
                               (om/transact! this `[(ops/add-checked-date! {:id ~id :date ~date})]))})))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--horizontal"}
        (doall
         (for [monday (take-nth 7 days)]
           (let [sunday (t/plus- monday (t/days 6))]
             (dom/span
              #js {:key (tf/unparse basic-date-formatter monday)
                   :className "calendar__label calendar__label--vertical"}
              (when (> 8 (t/day sunday))
                (tf/unparse month-label-formatter sunday)))))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--vertical"}
        (doall
         (for [day (take 7 days)]
           (dom/span
            #js {:key (tf/unparse basic-date-formatter day)
                 :className "calendar__label calendar__label--horizontal"}
            (when (odd? (t/day day))
              (tf/unparse day-label-formatter day)))))))

      (dom/div
       #js {:className "calendar__footer"})))))

(def ui-calendar (om/factory Calendar {:keyfn :calendar/id}))


(defui ^:once User
  static om/Ident
  (ident [_ props] [:user/by-id (:user/id props)])

  static om/IQuery
  (query
   [_]
   [:user/id
    :user/first-name
    :user/avatar-url
    {:user/calendars (om/get-query Calendar)}])

  Object
  (render
   [this]
   (let [{:user/keys [first-name avatar-url calendars]} (om/props this)]
     (dom/div
      #js {:className "user"}
      (dom/div
       #js {:className "user__details"}
       (dom/img
        #js {:className "user__details__avatar"
             :alt "user-details-avatar"
             :src avatar-url})
       (dom/span
        #js {:className "user__details__first-name"}
        first-name)

       (dom/div
        #js {:className "user__details__divider"}))

      (map ui-calendar calendars)))))

(def ui-user (om/factory User))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    :ui/loading-data
    {:current-user (om/get-query User)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key ui/loading-data current-user]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (if (or loading-data (empty? current-user))
        (dom/div
         #js {:className "loader"}
         "LOADING!!!")
        (dom/div
         #js {:className "page"}
         (ui-user current-user)))))))
