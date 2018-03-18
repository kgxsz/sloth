(ns app.operations
  (:require [fulcro.client.mutations :refer [defmutation]]))


(defmutation initialise-auth-attempt!
  [{:keys [tempid]}]
  (action [{:keys [state]}]
          (swap! state
                 assoc-in
                 [:home-page :page :auth-attempt-id]
                 tempid))
  (remote [env] true))


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
