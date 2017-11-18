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
       #js {:className "user__divider"})

      (dom/div
       #js {:className "user__options"}
       (dom/div
        #js {:className "user__options__ellipse"})
       (dom/div
        #js {:className "user__options__ellipse"})
       (dom/div
        #js {:className "user__options__ellipse"}))))))

(def ui-user (om/factory User))


(defui ^:once App
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    {:current-user (om/get-query User)}])
  static fc/InitialAppState
  (initial-state
   [_ _]
   {:current-user (fc/get-initial-state User {:first-name "Keigo"
                                              :avatar-url "images/avatar.jpg"})})

  Object
  (render
   [this]
   (let [{:keys [ui/react-key current-user]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app__header"}
       (ui-user current-user))
      (dom/div
       #js {:className "app__body"})))))
