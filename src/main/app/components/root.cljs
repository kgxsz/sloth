(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.navigation :as navigation]
            [app.utils :as u]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc get-query get-initial-state factory]]
            [fulcro.client.routing :refer-macros [defrouter]]))


;; TODO
;; Load user related data depending on the route param
;; Get sane defaults into production, include them in the base migration
;; Stop getting current user for now, it is all public
;; Ensure that you can hit a frontend route on a page reload
;; Intorduce an unknown-page handler and route
;; Look at loading states and good feedback


(defsc HomePage [this {:keys []}]
  {:initial-state {:page :home-page}
   :query [:page]}
  (dom/div
   ;; TODO - wrap this in a mutation that loads what it needs and then does a navigation
   #js {:onClick #(navigation/navigate {:handler :user-page
                                        :route-params {:first-name "keigo"}})}
   (ui-logo)))


(defsc UserPage [this {:keys [current-user]}]
  {:initial-state {:page :user-page}
   :query [:page
           {[:current-user '_] (get-query User)}]}
  (ui-user current-user))


(defrouter Pages :pages
  (ident [this props] [(:page props) :page])
  :home-page HomePage
  :user-page UserPage)


(def ui-pages (factory Pages))


(defsc Root [this {:keys [ui/react-key ui/loading-data current-user pages]}]
  {:initial-state (fn [_] (merge navigation/routing-tree {:pages (get-initial-state Pages {})}))
   :query [:ui/react-key
           :ui/loading-data
           :current-user
           {:pages (get-query Pages)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (dom/div
    #js {:className (u/bem [:page])}
    (if (or loading-data (empty? current-user))
      (ui-logo)
      (ui-pages pages)))))
