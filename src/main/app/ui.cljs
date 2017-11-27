(ns app.ui
  (:require [app.operations :as ops]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [fulcro.client.core :as fc]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

;; TODO - move these into a utils file
(def title-formatter (tf/formatter "EEEE do 'of' MMMM, Y"))
(def month-label-formatter (tf/formatter "MMM"))
(def key-formatter (tf/formatters :basic-date))

;; TODO - figure out where this goes in state
(def today (t/today))

(defui ^:once User
  static om/Ident
  (ident [_ props] [:user/by-id (:user/id props)])

  static om/IQuery
  (query
   [_]
   [:user/id
    :user/first-name
    :user/avatar-url])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [id first-name avatar-url]}]
   {:user/id id
    :user/first-name first-name
    :user/avatar-url avatar-url})

  Object
  (render
   [this]
   (let [{:user/keys [first-name avatar-url]} (om/props this)]
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


(defui ^:once Day
  static om/Ident
  (ident [_ props] [:day/by-id (:day/id props)])

  static om/IQuery
  (query
   [_]
   [:day/id
    :day/date
    :day/checked?
    :day/colour])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [id date checked? colour]}]
   {:day/id id
    :day/date date
    :day/checked? checked?
    :day/colour colour})

  Object
  (render
   [this]
   (let [{:day/keys [id date checked? colour]} (om/props this)]
     (dom/div
      #js {:title (tf/unparse title-formatter date)
           ;; TODO - time to use utils for BEM
           :className (str "calendar__days__day "
                           "calendar__days__day--"
                           (cond
                             checked? (name colour)
                             (odd? (t/month date)) "grey-medium"
                             :else "grey-light"))
           :onClick #(om/transact! this `[(ops/toggle-day-checked?! {:id ~id})])}))))

(def ui-day (om/factory Day))


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
    {:calendar/days (om/get-query Day)}])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [id title subtitle colour]}]
   {:calendar/id id
    :calendar/title title
    :calendar/subtitle subtitle
    :calendar/colour colour
    :calendar/days [] #_(into []
                         (for [date (->> today
                                         (iterate #(t/minus- % (t/days 1)))
                                         (take (+ 357 (t/day-of-week today)))
                                         (reverse))]
                           (fc/get-initial-state Day {:id (random-uuid)
                                                      :date date
                                                      :checked? false
                                                      :colour colour})))})

  Object
  (render
   [this]
   (let [{:calendar/keys [title subtitle days]} (om/props this)]
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
        (map ui-day days))

       (dom/div
        #js {:className "calendar__labels calendar__labels--horizontal"}
        (doall
         (for [last-day-of-the-week (->> (t/plus- today (t/days (- 7 (t/day-of-week today))))
                                         (iterate #(t/minus- % (t/weeks 1)))
                                         (take 52))]
           (let [hidden? (> (t/day last-day-of-the-week) 7)]
             (dom/span
              #js {:key (tf/unparse key-formatter last-day-of-the-week)
                   ;; TODO - time to use utils for BEM
                   :className (str "calendar__label calendar__label--vertical "
                                   (when hidden? "calendar__label--hidden"))}
              (tf/unparse month-label-formatter last-day-of-the-week))))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--vertical"}
        (doall
         (for [day-label ["Mon" "Wed" "Fri" "Sun"]]
           (dom/span
            #js {:key day-label
                 :className "calendar__label calendar__label--horizontal"}
            day-label)))))

      (dom/div
       #js {:className "calendar__footer"})))))

(def ui-calendar (om/factory Calendar))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    {:user (om/get-query User)}
    {:calendars (om/get-query Calendar)}])

  static fc/InitialAppState
  (initial-state
   [_ _]
   {:user nil
    :calendars []})

  Object
  (render
   [this]
   (let [{:keys [ui/react-key user calendars]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (ui-user user)
       (map ui-calendar calendars))))))
