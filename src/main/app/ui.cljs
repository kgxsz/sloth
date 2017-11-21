(ns app.ui
  (:require [app.operations :as ops]
            [app.schema :as schema]
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
         (for [day-label ["mon" "wed" "fri" "sun"]]
           (dom/div
            #js {:key day-label
                 :className "task__day-label-container"}
            (dom/span
             #js {:className "task__label"}
             day-label)))))

       (dom/div
        #js {:className "task__calendar__section-right"}
        (dom/div
         #js {:className "task__calendar__section-right__insulator"}
         (dom/div
          #js {:className "task__days"}
          (doall
           (for [day (range 364)]
             (dom/div
              #js {:key day
                   :className "task__days__day"}))))

         (dom/div
          #js {:className "task__month-labels"}
          (doall
           (for [month-label ["jan" "feb" "mar" "apr" "may" "jun" "jul" "aug" "sep" "oct" "nov" "dec"]]
             (dom/div
              #js {:key month-label
                   :className "task__month-labels__month-label-container"}
              (dom/span
               #js {:className "task__label task__label--vertical"}
               month-label))))))))

      (dom/div
       #js {:className "task__footer"})))))

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
