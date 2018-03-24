(ns app.operations
  (:require [fulcro.client.data-fetch :as data.fetch]
            [fulcro.client.mutations :refer [defmutation]]))


(defmutation initialise-auth-attempt!
  [_]
  (action [{:keys [state] :as env}]
          (data.fetch/load-action env :auth-attempt app.components.root/AuthAttempt
                                  {:target [:home-page :page :auth-attempt]}))
  (remote [env] (data.fetch/remote-load env)))


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
