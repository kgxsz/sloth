(ns app.components.root
  (:require [app.operations :as ops]
            [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.navigation :as navigation]
            [app.utils :as u]
            [fulcro.client.data-fetch :as data]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc transact! get-query get-initial-state factory]]
            [fulcro.client.routing :refer-macros [defrouter]]))


;; TODO
;; Ensure that you can hit a frontend route on a page reload
;; Load user related data from route-params url being set manually
;; Intorduce an unknown-page handler and route
;; Look at loading states and good feedback


(defsc HomePage [this {:keys []}]
  {:initial-state {:page :home-page}
   :query [:page]}
  (dom/div
   #js {:onClick (fn []
                   (data/load this :user User {:params {:first-name "Keigo"}
                                               :target [:user-page :page :user]})
                   (navigation/navigate {:handler :user-page
                                         :route-params {:first-name "Keigo"}}))}
   (ui-logo)))


(defsc UserPage [this {:keys [user]}]
  {:initial-state {:page :user-page}
   :query [:page
           {:user (get-query User)}]}
  (if (empty? user)
    (ui-logo)
    (ui-user user)))


(defsc UnknownPage [this {:keys []}]
  {:initial-state {:page :unknown-page}
   :query [:page]}
  (dom/div
   nil
   "You're lost :("))


(defrouter Pages :pages
  (ident [this props] [(:page props) :page])
  :home-page HomePage
  :user-page UserPage
  :unknown-page UnknownPage)


(def ui-pages (factory Pages))


(defsc Root [this {:keys [ui/react-key pages]}]
  {:initial-state (fn [_] (merge navigation/routing-tree {:pages (get-initial-state Pages {})}))
   :query [:ui/react-key
           {:pages (get-query Pages)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (dom/div
    #js {:className (u/bem [:page])}
    (ui-pages pages))))
 
