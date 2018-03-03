(ns app.components.calendar
  (:require [app.operations :as operations]
            [app.utils :as u]
            [cljs-time.core :as t]
            [cljs-time.format :as t.format]
            [cljs-time.periodic :as t.periodic]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as fulcro :refer [defsc factory]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; helpers

(def month-label-formatter (t.format/formatter "MMM"))
(def day-label-formatter (t.format/formatter "E"))
(def date-label-formatter (t.format/formatter "EEEE do 'of' MMMM, Y"))
(def basic-formatter (t.format/formatters :basic-date))

(def make-items
  (memoize
   (fn [today]
     (for [date (t.periodic/periodic-seq (t/minus- today (t/days (+ 356 (t/day-of-week today))))
                                         (t/plus- today (t/days 1))
                                         (t/days 1))]
       {:date (t.format/unparse basic-formatter date)
        :label (t.format/unparse date-label-formatter date)
        :shaded? (odd? (t/month date))}))))

(def make-horizontal-labels
  (memoize
   (fn [today]
     (for [date (t.periodic/periodic-seq (t/minus- today (t/days (+ 350 (t/day-of-week today))))
                                         (t/plus- today (t/days (- 8 (t/day-of-week today))))
                                         (t/weeks 1))]
       {:date (t.format/unparse basic-formatter date)
        :label (t.format/unparse month-label-formatter date)
        :visible? (> 8 (t/day date))}))))

(def make-vertical-labels
  (memoize
   (fn [today]
     (for [date (t.periodic/periodic-seq (t/minus- today (t/days (- (t/day-of-week today) 1)))
                                         (t/plus- today (t/days (- 8 (t/day-of-week today))))
                                         (t/days 1))]
       {:date (t.format/unparse basic-formatter date)
        :label (t.format/unparse day-label-formatter date)
        :visible? (odd? (t/day-of-week date))}))))

(def colour-options
  {:a :colour-green-dark
   :b :colour-yellow-dark
   :c :colour-purple-dark
   :d :colour-blue-dark})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; component

(defsc Calendar [this {:keys [db/id] :calendar/keys [title subtitle colour-option checked-dates]}]
  {:ident [:calendar/by-id :db/id]
   :query [:db/id
           :calendar/title
           :calendar/subtitle
           :calendar/colour-option
           :calendar/checked-dates]}
  (dom/div
   #js {:className (u/bem [:calendar])}
   (dom/div
    #js {:className (u/bem [:calendar__header])}
    (dom/span
     #js {:className (u/bem [:text :heading-medium :font-weight-bold :colour-black-light])}
     title)
    (dom/span
     #js {:className (u/bem [:calendar__header__separator])}
     (dom/span
      #js {:className (u/bem [:text :heading-medium :colour-grey-dark])}
      "—"))
    (dom/span
     #js {:className (u/bem [:text :heading-medium :colour-grey-dark])}
     subtitle))

   (dom/div
    #js {:className (u/bem [:calendar__body])}
    (dom/div
     #js {:className (u/bem [:calendar__items])}
     (doall
      (for [{:keys [date label shaded?]} (make-items (t/today))]
        (let [checked? (contains? (set checked-dates) date)]
          (dom/div
           #js {:key date
                :title label
                :className (u/bem [:calendar__items__item
                                   (cond
                                     checked? (get colour-options colour-option)
                                     shaded? :colour-grey-medium
                                     :else :colour-grey-light)])
                :onClick #(if checked?
                            (fulcro/transact! this `[(operations/remove-checked-date! {:id ~id :date ~date})])
                            (fulcro/transact! this `[(operations/add-checked-date! {:id ~id :date ~date})]))})))))

    (dom/div
     #js {:className (u/bem [:calendar__labels :horizontal])}
     (doall
      (for [{:keys [date label visible?]} (make-horizontal-labels (t/today))]
        (dom/div
         #js {:key date
              :className (u/bem [:calendar__label :vertical])}
         (when visible?
           (dom/span
            #js {:className (u/bem [:text :paragraph-small :font-weight-bold])}
            label))))))

    (dom/div
     #js {:className (u/bem [:calendar__labels :vertical])}
     (doall
      (for [{:keys [date label visible?]} (make-vertical-labels (t/today))]
        (dom/div
         #js {:key date
              :className (u/bem [:calendar__label :horizontal])}
         (when visible?
           (dom/span
            #js {:className (u/bem [:text :paragraph-small :font-weight-bold])}
            label)))))))

   (dom/div
    #js {:className (u/bem [:calendar__footer])})))

(def ui-calendar (factory Calendar {:keyfn :db/id}))
