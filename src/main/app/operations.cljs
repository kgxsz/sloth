(ns app.operations
  (:require [fulcro.client.mutations :as m :refer [defmutation]]))

(defmutation toggle-day-checked?!
  [{:keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [:day/by-id id :day/checked?] not)))
