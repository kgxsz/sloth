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
   (let [{:calendar/keys [id title subtitle colour checked-dates]} (om/props this)]
     (dom/div
      #js {:className "calendar"}
      (dom/div
       #js {:className "calendar__header"}
       (dom/span
        #js {:className "text text--heading-medium text--font-weight-bold text--colour-black-light"}
        title)
       (dom/span
        #js {:className "calendar__header__separator"}
        (dom/span
         #js {:className "text text--heading-medium text--colour-grey-dark"}
         "—"))
       (dom/span
        #js {:className "text text--heading-medium text--colour-grey-dark"}
        subtitle))

      (dom/div
       #js {:className "calendar__body"}
       (dom/div
        #js {:className "calendar__items"}
        (doall
         (for [{:keys [date label shaded?]} (make-items (t/today))]
           (let [checked? (contains? checked-dates date)]
             (dom/div
              #js {:key date
                   :title label
                   ;; TODO - time to use utils for BEM
                   :className (str "calendar__items__item"
                                   " calendar__items__item--colour-"
                                   (cond
                                     checked? (str (name colour) "-dark")
                                     shaded? "grey-medium"
                                     :else "grey-light"))
                   :onClick #(if checked?
                               (om/transact! this `[(ops/remove-checked-date! {:id ~id :date ~date})])
                               (om/transact! this `[(ops/add-checked-date! {:id ~id :date ~date})]))})))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--horizontal"}
        (doall
         (for [{:keys [date label visible?]} (make-horizontal-labels (t/today))]
           (dom/div
            #js {:key date
                 :className "calendar__label calendar__label--vertical"}
            (when visible?
              (dom/span
               #js {:className "text text--paragraph-small text--font-weight-bold"}
               label))))))

       (dom/div
        #js {:className "calendar__labels calendar__labels--vertical"}
        (doall
         (for [{:keys [date label visible?]} (make-vertical-labels (t/today))]
           (dom/div
            #js {:key date
                 :className "calendar__label calendar__label--horizontal"}
            (when visible?
              (dom/span
               #js {:className "text text--paragraph-small text--font-weight-bold"}
               label)))))))

      (dom/div
       #js {:className "calendar__footer"})))))

(def ui-calendar (om/factory Calendar {:keyfn :calendar/id}))


(defui ^:once UserDetails
  Object
  (render
   [this]
   (let [{{:keys [first-name]} :names {:keys [url]} :avatar} (om/props this)]
     (dom/div
      #js {:className "user-details"}
      (dom/img
       #js {:className "user-details__avatar"
            :alt "user-details-avatar"
            :src url})
      (dom/div
       #js {:className "user-details__first-name"}
       (dom/span
        #js {:className "text text--font-weight-bold text--heading-medium text--ellipsis"}
        first-name))
      (dom/div
       #js {:className "user-details__divider"})))))

(def ui-user-details (om/factory UserDetails))


(defui ^:once User
  static om/Ident
  (ident [_ props] [:user/by-id (:user/id props)])

  static om/IQuery
  (query
   [_]
   [:user/id
    {:user/names [:first-name :last-name]}
    {:user/avatar [:url]}
    {:user/calendars (om/get-query Calendar)}])

  Object
  (render
   [this]
   (let [{:user/keys [names avatar calendars]} (om/props this)]
     (dom/div
      #js {:className "user"}
      (ui-user-details {:names names :avatar avatar})
      (map ui-calendar calendars)))))

(def ui-user (om/factory User))


(defui ^:once Logo
  Object
  (render
   [this]
   (dom/div
    #js {:className "logo"}
    (dom/svg
     #js {:xmlns "http://www.w3.org/2000/svg"
          :viewBox "0 0 64 42"}
     (dom/g
      nil
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-dark"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(55 33)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-medium"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(44 33)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-medium"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(55 22)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-dark"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(44 22)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-10.687-8.008h2.064l1.952,2.848a.351.351,0,0,0,.256.16h2.128c.085,0,.128-.038.128-.112a.4.4,0,0,0-.1-.208l-1.615-2.368a5.551,5.551,0,0,0-.512-.64v-.031a2.969,2.969,0,0,0,2.16-2.88,3.071,3.071,0,0,0-1.124-2.466,4.485,4.485,0,0,0-2.876-.894H24.328a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192H26.12a.208.208,0,0,0,.193-.192v-2.815Zm2.319-1.728H26.312v-3.04h2.319c1.214,0,1.968.589,1.968,1.536C30.6,1038.688,29.846,1039.264,28.632,1039.264Z"
           :transform "translate(4 -1007)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-medium"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-13.672-14.6a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192h4.08a5.6,5.6,0,0,0,3.9-1.314,4.61,4.61,0,0,0,1.38-3.486,4.513,4.513,0,0,0-1.516-3.5,5.711,5.711,0,0,0-3.8-1.3Zm4.08,7.776h-2.1v-5.952h2.065a2.9,2.9,0,0,1,3.136,2.976A2.769,2.769,0,0,1,27.408,1042.176Z"
           :transform "translate(-18 -1007)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-9.672-14.6a.208.208,0,0,0-.192.193v9.216a.208.208,0,0,0,.192.192H29.12a.208.208,0,0,0,.192-.192v-9.216a.208.208,0,0,0-.192-.193Z"
           :transform "translate(26 -1029)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-medium"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-10.687-8.008h2.064l1.952,2.848a.351.351,0,0,0,.256.16h2.128c.085,0,.128-.038.128-.112a.4.4,0,0,0-.1-.208l-1.615-2.368a5.551,5.551,0,0,0-.512-.64v-.031a2.969,2.969,0,0,0,2.16-2.88,3.071,3.071,0,0,0-1.124-2.466,4.485,4.485,0,0,0-2.876-.894H24.328a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192H26.12a.208.208,0,0,0,.193-.192v-2.815Zm2.319-1.728H26.312v-3.04h2.319c1.214,0,1.968.589,1.968,1.536C30.6,1038.688,29.846,1039.264,28.632,1039.264Z"
           :transform "translate(4 -1029)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-9.064-14.76a4.967,4.967,0,0,0-5.3,4.96,4.711,4.711,0,0,0,1.562,3.62,5.565,5.565,0,0,0,3.734,1.34,5.326,5.326,0,0,0,3.77-1.316,4.311,4.311,0,0,0,1.286-3.164v-.8h-5.76a.271.271,0,0,0-.224.224v1.216a.271.271,0,0,0,.224.224h3.536c-.061.891-1.055,1.792-2.832,1.792a3.185,3.185,0,0,1-2.178-.806,3.2,3.2,0,0,1,2.178-5.466,2.388,2.388,0,0,1,2.592,1.68.226.226,0,0,0,.24.16h1.648c.188,0,.256-.107.256-.208a3.254,3.254,0,0,0-1.138-2.262A5.206,5.206,0,0,0,27.936,1034.24Z"
           :transform "translate(-18 -1029)"}))))))


(def ui-logo (om/factory Logo))


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
      #js {:key react-key}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (if (or loading-data (empty? current-user))
         (ui-logo)
         (ui-user current-user)))))))
