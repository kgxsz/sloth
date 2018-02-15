(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :as prim :refer [defsc get-query factory]]
            [fulcro.client.routing :as r :refer-macros [defrouter]]))


(defsc HomePage [this {:keys []}]
  {:initial-state {:page :home-page}
   :query [:page]}
  (dom/div
   #js {:onClick #(prim/transact! this `[(r/route-to {:handler :keigo-page})])}
   (ui-logo)))


(defsc KeigoPage [this {:keys [current-user]}]
  {:initial-state {:page :keigo-page}
   :query [:page
           {[:current-user '_] (get-query User)}]}
  (ui-user current-user))


(defrouter Router :router
  (ident [this props] [(:page props) :main])
  :home-page HomePage
  :keigo-page KeigoPage)


(def ui-router (factory Router))


(def routing-tree
  (r/routing-tree
    (r/make-route :home-page [(r/router-instruction :router [:home-page :main])])
    (r/make-route :keigo-page [(r/router-instruction :router [:keigo-page :main])])))


(defsc Root [this {:keys [ui/react-key ui/loading-data router current-user]}]
  {:initial-state (fn [params]
                    (merge routing-tree {:router (prim/get-initial-state Router {})}))
   :query [:ui/react-key
           :ui/loading-data
           {:router (prim/get-query Router)}
           {:current-user (get-query User)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (dom/div
    #js {:className (u/bem [:page])}
    (if (or loading-data (empty? current-user))
      (ui-logo)
      (ui-router router)))))
