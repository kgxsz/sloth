(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.notification :refer [ui-notification]]
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
      (ui-notification {:title "Your screen is too narrow"
                        :paragraph "This application needs a wider screen size to work properly."
                        :only-visible-on-tiny-width? true})
      ;; TODO - kill this when notifications is done
      #_(dom/div
       #js {:className (u/bem [:app-error-notice])}
       "You need to use a wider screen.")
      (dom/div
       #js {:className (u/bem [:page])}
       (if (or loading-data (empty? current-user))
         (ui-logo)
         (ui-user current-user)))))))
