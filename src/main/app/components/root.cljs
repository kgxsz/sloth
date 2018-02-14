(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as prim :refer [defsc get-query]]))

(defsc Root [this {:keys [ui/react-key ui/loading-data current-user]}]
  {:query [:ui/react-key
           :ui/loading-data
           {:current-user (get-query User)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (dom/div
    #js {:className (u/bem [:page])}
    (if (or loading-data (empty? current-user))
        (ui-logo)
        (ui-user current-user)))))
