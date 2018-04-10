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
     #js {:className (u/bem [:icon :warning :paragraph-huge :colour-yellow-dark])})
    (dom/div
     #js {:className (u/bem [:text :paragraph-large :font-weight-bold :colour-yellow-dark :padding-left-xxx-small])}
     title))
   (dom/div
    #js {:className (u/bem [:notification__paragraph])}
    (dom/div
     #js {:className (u/bem [:text :heading-small :colour-yellow-dark])}
     paragraph))))

(def ui-notification (factory Notification))
