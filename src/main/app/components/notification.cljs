(ns app.components.notification
  (:require [app.utils :as u]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once Notification
  Object
  (render
   [this]
   (let [{:keys [title paragraph]} (om/props this)]
     (dom/div
      #js {:className (u/bem [:notification])}
      (dom/div
       #js {:className (u/bem [:notification__body])}
       (dom/div
        #js {:className (u/bem [:notification__body__lip])}
        (dom/div
         #js {:className (u/bem [:icon :warning :heading-medium :colour-white-light])}))
       (dom/div
        #js {:className (u/bem [:notification__body__content])}
        (dom/div
         #js {:className (u/bem [:notification__body__content__title])}
         (dom/div
          #js {:className (u/bem [:text :heading-small :font-weight-bold :colour-red-dark])}
          title))
        (dom/div
         #js {:className (u/bem [:text :paragraph-small])}
         paragraph)))))))

(def ui-notification (om/factory Notification))
