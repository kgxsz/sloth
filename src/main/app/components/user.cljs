(ns app.components.user
  (:require [app.components.user-details :refer [ui-user-details]]
            [app.components.calendar :refer [ui-calendar Calendar]]
            [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as fuclro :refer [defsc get-query factory]]))

(defsc User [this {:keys [db/id] :user/keys [first-name last-name avatar-url calendars]}]
  {:ident [:user/by-id :db/id]
   :query [:db/id
           :user/first-name
           :user/last-name
           :user/created-at
           :user/facebook-id
           :user/roles
           :user/avatar-url
           {:user/calendars (get-query Calendar)}
           :ui/fetch-state]}
  (dom/div
   #js {:className (u/bem [:user])}
   (ui-user-details {:first-name first-name :last-name last-name :avatar-url avatar-url})
   (map ui-calendar calendars)))

(def ui-user (factory User {:keyfn :db/id}))
