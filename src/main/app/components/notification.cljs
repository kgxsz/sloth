(ns app.components.notification
  (:require [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc factory]]))

(defsc Notification [this {:keys [title paragraph]}]
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
       #js {:className (u/bem [:text :heading-small :font-weight-bold :colour-yellow-dark])}
       title))
     (dom/div
      #js {:className (u/bem [:text :paragraph-small])}
      paragraph)))))

(def ui-notification (factory Notification))
