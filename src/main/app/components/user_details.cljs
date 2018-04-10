(ns app.components.user-details
  (:require [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc factory]]))

(defsc UserDetails [this {:keys [first-name last-name avatar-url]}]
  (dom/div
   #js {:className (u/bem [:user-details])}
   (dom/img
    #js {:className (u/bem [:user-details__avatar])
         :alt "user-details-avatar"
         :src avatar-url})
   (dom/div
    #js {:className (u/bem [:user-details__first-name])}
    (dom/span
     #js {:className (u/bem [:text :font-weight-bold :font-size-huge :ellipsis])}
     first-name))
   (dom/div
    #js {:className (u/bem [:user-details__divider])})))

(def ui-user-details (factory UserDetails))
