(ns app.components.user-details
  (:require [app.utils :as u]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once UserDetails
  Object
  (render
   [this]
   (let [{{:keys [first-name]} :names {:keys [url]} :avatar} (om/props this)]
     (dom/div
      #js {:className (u/bem [:user-details])}
      (dom/img
       #js {:className (u/bem [:user-details__avatar])
            :alt "user-details-avatar"
            :src url})
      (dom/div
       #js {:className (u/bem [:user-details__first-name])}
       (dom/span
        #js {:className (u/bem [:text :font-weight-bold :heading-medium :ellipsis])}
        first-name))
      (dom/div
       #js {:className (u/bem [:user-details__divider])})))))

(def ui-user-details (om/factory UserDetails))
