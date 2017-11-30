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
(def month-label-formatter (tf/formatter "MMM"))
(def day-label-formatter (tf/formatter "E"))
(def date-label-formatter (tf/formatter "EEEE do 'of' MMMM, Y"))
(def basic-formatter (tf/formatters :basic-date))

;; TODO - put these helpers somewhere where it makes sense
(def make-items
  (memoize
   (fn [today]
     (for [date (tpc/periodic-seq (t/minus- today (t/days (+ 356 (t/day-of-week today))))
                                  (t/plus- today (t/days 1))
                                  (t/days 1))]
       {:date (tf/unparse basic-formatter date)
        :label (tf/unparse date-label-formatter date)
        :shaded? (odd? (t/month date))}))))

(def make-horizontal-labels
  (memoize
   (fn [today]
     (for [date (tpc/periodic-seq (t/minus- today (t/days (+ 350 (t/day-of-week today))))
                                  (t/plus- today (t/days (- 8 (t/day-of-week today))))
                                  (t/weeks 1))]
       {:date (tf/unparse basic-formatter date)
        :label (tf/unparse month-label-formatter date)
        :visible? (> 8 (t/day date))}))))

(def make-vertical-labels
  (memoize
   (fn [today]
     (for [date (tpc/periodic-seq (t/minus- today (t/days (- (t/day-of-week today) 1)))
                                  (t/plus- today (t/days (- 8 (t/day-of-week today))))
                                  (t/days 1))]
       {:date (tf/unparse basic-formatter date)
        :label (tf/unparse day-label-formatter date)
        :visible? (odd? (t/day date))}))))


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
   (let [{:calendar/keys [id title subtitle colour checked-dates]} (om/props this)
         ;; TODO - figure out where to put this guy
         today (t/today)]
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
        ;; TODO - change the css to items
        #js {:className "calendar__items"}
        (doall
         (for [{:keys [date label shaded?]} (make-items today)]
           (let [checked? (contains? checked-dates date)]
             (dom/div
              #js {:key date
                   :title label
                   ;; TODO - time to use utils for BEM
                   :className (str "calendar__items__item"
                                   " calendar__items__item--"
                                   (cond
                                     checked? (name colour)
                                     shaded? "grey-medium"
                                     :else "grey-light"))
                   :onClick #(if checked?
                               (om/transact! this `[(ops/remove-checked-date! {:id ~id :date ~date})])
                               (om/transact! this `[(ops/add-checked-date! {:id ~id :date ~date})]))})))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--horizontal"}
        (doall
         (for [{:keys [date label visible?]} (make-horizontal-labels today)]
           (dom/span
            #js {:key date
                 :className "calendar__label calendar__label--vertical"}
            (when visible?
              label)))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--vertical"}
        (doall
         (for [{:keys [date label visible?]} (make-vertical-labels today)]
           (dom/span
            #js {:key date
                 :className "calendar__label calendar__label--horizontal"}
            (when visible?
              label))))))

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
