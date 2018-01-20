(ns app.components.user
  (:require [app.components.user-details :refer [ui-user-details]]
            [app.components.calendar :refer [ui-calendar Calendar]]
            [app.utils :as u]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once User
  static om/Ident
  (ident [_ props] [:user/by-id (:user/id props)])

  static om/IQuery
  (query
   [_]
   [:user/id
    :user/first-name
    :user/last-name
    :user/avatar-url
    {:user/calendars (om/get-query Calendar)}])

  Object
  (render
   [this]
   (let [{:user/keys [first-name last-name avatar-url calendars]} (om/props this)]
     (dom/div
      #js {:className (u/bem [:user])}
      (ui-user-details {:first-name first-name :last-name last-name :avatar-url avatar-url})
      (map ui-calendar calendars)))))

(def ui-user (om/factory User))
