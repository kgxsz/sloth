(ns app.operations
  (:require
   [fulcro.client.data-fetch :as data]
            [fulcro.client.mutations :as m :refer [defmutation]]
            [fulcro.client.routing :as routing]))


(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [{:keys [state]}]
          (swap! state
                 update-in
                 [:calendar/by-id id :calendar/checked-dates]
                 #(-> % set (conj date) vec)))
  (remote [env] true))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [{:keys [state]}]
          (swap! state
                 update-in
                 [:calendar/by-id id :calendar/checked-dates]
                 #(-> % set (disj date) vec)))
  (remote [env] true))


(defmutation route-to!
  [{:keys [handler route-params]}]
  (action [env]
          (case handler
            :user-page (data/load-action env
                                         :user
                                         app.components.user/User
                                         {:params (select-keys route-params [:first-name])
                                          :target [:user-page :page :user]})
            nil)
          #_(routing/route-to-impl! env {:handler handler :route-params route-params}))
  (remote [env] (data/remote-load env)))
