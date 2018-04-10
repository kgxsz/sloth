(ns app.components.notification
  (:require [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc factory]]))

(defsc Notification [this {:keys [title paragraph]}]
  (dom/div
   #js {:className (u/bem [:notification])}
   (dom/div
    #js {:className (u/bem [:notification__title])}
    (dom/div
     #js {:className (u/bem [:icon :warning :font-size-xxx-large :colour-yellow-dark])})
    (dom/div
     #js {:className (u/bem [:text :font-size-x-large :font-weight-bold :colour-yellow-dark :padding-left-xxx-small])}
     title))
   (dom/div
    #js {:className (u/bem [:notification__paragraph])}
    (dom/div
     #js {:className (u/bem [:text :font-size-medium :colour-yellow-dark])}
     paragraph))))

(def ui-notification (factory Notification))
