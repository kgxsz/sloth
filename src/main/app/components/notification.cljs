(ns app.components.notification
  (:require [app.utils :as u]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once Notification
  Object
  (render
   [this]
   (dom/div
    #js {:className (u/bem [:notification])}
    "this is a notification")))

(def ui-notification (om/factory Notification))
