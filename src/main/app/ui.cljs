(ns app.ui
  (:require [app.operations :as ops]
            [app.schema :as schema]
            [fulcro.client.core :as fc]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]
            [cljs.spec.alpha :as spec]))

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


(defui ^:once Grid
  static om/IQuery
  (query
   [_]
   [:grid/title
    :grid/subtitle])

  static fc/InitialAppState
  (initial-state
   [_ {:keys [title subtitle]}]
   {:grid/title title
    :grid/subtitle subtitle})

  Object
  (render
   [this]
   (let [{:keys [grid/title grid/subtitle]} (om/props this)]
     (dom/div
      #js {:className "grid"}
      (dom/div
       #js {:className "grid__header"}
       (dom/span
        #js {:className "grid__header__title"}
        title)
       (dom/span
        #js {:className "grid__header__divider"}
        "—")
       (dom/span
        #js {:className "grid__header__subtitle"}
        subtitle))))))

(def ui-grid (om/factory Grid))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    {:user (om/get-query User)}
    {:grid (om/get-query Grid)}])
  static fc/InitialAppState
  (initial-state
   [_ _]
   {:user (fc/get-initial-state User {:first-name "Keigo"
                                      :avatar-url "images/avatar.jpg"})
    :grid (fc/get-initial-state Grid {:title "Coding"
                                      :subtitle "at least 1 commit"})})

  Object
  (render
   [this]
   (let [{:keys [ui/react-key user grid]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "notice notice--hidden"}
       "! You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (ui-user user)
       (ui-grid grid))))))
