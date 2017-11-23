(ns app.ui
  (:require [app.operations :as ops]
            [app.schema :as schema]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
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


(defui ^:once Task
  static om/IQuery
  (query
   [_]
   [:task/title
    :task/subtitle])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [title subtitle]}]
   {:task/title title
    :task/subtitle subtitle})

  Object
  (render
   [this]
   (let [{:keys [task/title task/subtitle]} (om/props this)]
     (dom/div
      #js {:className "task"}
      (dom/div
       #js {:className "task__header"}
       (dom/span
        #js {:className "task__header__title"}
        title)
       (dom/span
        #js {:className "task__header__separator"}
        "—")
       (dom/span
        #js {:className "task__header__subtitle"}
        subtitle))

      (dom/div
       #js {:className "task__calendar"}
       (dom/div
        #js {:className "task__calendar__section-left"}
        (doall
         (for [day-label ["Mon" "Wed" "Fri" "Sun"]]
           (dom/div
            #js {:key day-label
                 :className "task__day-label-container"}
            (dom/span
             #js {:className "task__calendar__label"}
             day-label)))))

       (dom/div
        #js {:className "task__calendar__section-right"}
        (dom/div
         #js {:className "task__calendar__section-right__insulator"}
         (dom/div
          #js {:className "task__days"}
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
                     :className "task__days__day"})))))

 (dom/div
          #js {:className "task__month-labels"}
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
                       :className "task__month-labels__month-label-container"}
                  (when show?
                    (dom/span
                     #js {:className "task__calendar__label task__calendar__label--vertical"}
                     (tf/unparse formatter sunday))))))))))))

      (dom/div
       #js {:className "task__footer"})))))

(def next-sunday (t/plus- (t/today) (t/days (- 7 (t/day-of-week (t/today))))))

(->> next-sunday
     (iterate #(t/minus- % (t/weeks 1)))
     (take 52)
     (map (partial tf/unparse (tf/formatter "MMM"))))

(tf/unparse (tf/formatters :basic-date) next-sunday)

(def ui-task (om/factory Task))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    {:user (om/get-query User)}
    {:task (om/get-query Task)}])
  static fc/InitialAppState
  (initial-state
   [_ _]
   {:user (fc/get-initial-state User {:first-name "Keigo"
                                      :avatar-url "images/avatar.jpg"})
    :task (fc/get-initial-state Task {:title "Some task"
                                      :subtitle "some condition of satisfaction"})})

  Object
  (render
   [this]
   (let [{:keys [ui/react-key user task]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (ui-user user)
       (ui-task task))))))
