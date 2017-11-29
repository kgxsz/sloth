(ns app.operations
  (:require [fulcro.client.mutations :as m :refer [defmutation]]))

(defmutation add-checked-date!
  [{:keys [id date]}]
  (action [{:keys [state]}]
          (swap! state update-in [:calendar/by-id id :calendar/checked-dates] conj date))
  (remote [env] true))

(defmutation remove-checked-date!
  [{:keys [id date]}]
  (action [{:keys [state]}]
          (swap! state update-in [:calendar/by-id id :calendar/checked-dates] disj date))
  (remote [env] true))

