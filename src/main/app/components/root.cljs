(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.utils :as u]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once Root
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    :ui/loading-data
    {:current-user (om/get-query User)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key ui/loading-data current-user]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className (u/bem [:app])}
      (dom/div
       #js {:className (u/bem [:page])}
       (if (or loading-data (empty? current-user))
         (ui-logo)
         (ui-user current-user)))))))
