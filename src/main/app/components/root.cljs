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


(defrouter Pages :pages
  (ident [this props] [(:page props) :page])
  :home-page HomePage
  :keigo-page KeigoPage)


(def ui-pages (factory Pages))


(def routing-tree
  (r/routing-tree
    (r/make-route :home-page [(r/router-instruction :pages [:home-page :page])])
    (r/make-route :keigo-page [(r/router-instruction :pages [:keigo-page :page])])))


(defsc Root [this {:keys [ui/react-key ui/loading-data pages current-user]}]
  {:initial-state (fn [_] (merge routing-tree {:pages (prim/get-initial-state Pages {})}))
   :query [:ui/react-key
           :ui/loading-data
           :current-user
           {:pages (get-query Pages)}
           ]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (dom/div
    #js {:className (u/bem [:page])}
    (if (or loading-data (empty? current-user))
      (ui-logo)
      (ui-pages pages)))))
