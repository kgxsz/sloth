
(ns app.components.sad-message
  (:require [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc factory]]))

(defsc SadMessage [this {:keys [message]}]
  (dom/div
   #js {:className (u/bem [:sad-message])}
   (dom/div
    #js {:className (u/bem [:text :heading-huge :font-weight-bold :colour-black-light])}
    ":(")
   (dom/div
    #js {:className (u/bem [:text :heading-medium :font-weight-bold :colour-grey-dark :margin-top-medium])}
    message)))

(def ui-sad-message (factory SadMessage))
