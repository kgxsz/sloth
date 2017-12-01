(ns app.components.user-details
  (:require [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once UserDetails
  Object
  (render
   [this]
   (let [{{:keys [first-name]} :names {:keys [url]} :avatar} (om/props this)]
     (dom/div
      #js {:className "user-details"}
      (dom/img
       #js {:className "user-details__avatar"
            :alt "user-details-avatar"
            :src url})
      (dom/div
       #js {:className "user-details__first-name"}
       (dom/span
        #js {:className "text text--font-weight-bold text--heading-medium text--ellipsis"}
        first-name))
      (dom/div
       #js {:className "user-details__divider"})))))

(def ui-user-details (om/factory UserDetails))
