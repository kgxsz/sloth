(ns app.ui
  (:require [app.operations :as ops]
            [app.schema :as schema]
            [fulcro.client.core :as fc]
            [om.dom :as dom]
            [om.next :as om :refer [defui]]
            [cljs.spec.alpha :as spec]))

(defui ^:once App
  static om/IQuery
  (query [this] [:ui/react-key])
  static fc/InitialAppState
  (initial-state [c params] {})
  Object
  (render [this]
          (let [{:keys [ui/react-key]} (om/props this)]
            (dom/div
             #js {:key react-key
                  :className "app"}
             ))))
