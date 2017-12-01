(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :as logo]
            [app.components.user :as user]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once Root
  static om/IQuery
  (query
   [_]
   [:ui/react-key
    :ui/loading-data
    {:current-user (om/get-query user/User)}])

  Object
  (render
   [this]
   (let [{:keys [ui/react-key ui/loading-data current-user]} (om/props this)]
     (dom/div
      #js {:key react-key
           :className "app"}
      (dom/div
       #js {:className "app-error-notice"}
       "You need to use a wider screen.")
      (dom/div
       #js {:className "page"}
       (if (or loading-data (empty? current-user))
         (logo/ui-logo)
         (user/ui-user current-user)))))))
