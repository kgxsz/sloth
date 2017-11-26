(ns app.operations
  (:require [fulcro.client.mutations :as m :refer [defmutation]]))


(defmutation toggle-item-checked?!
  [{:keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [:item/by-id id :item/checked?] not)))
