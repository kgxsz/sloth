(ns app.components.root
  (:require [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.navigation :as navigation]
            [app.utils :as u]
            [fulcro.client.data-fetch :as data.fetch]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc get-query get-initial-state factory]]
            [fulcro.client.routing :refer-macros [defrouter]]))


(defsc HomePage [this {:keys []}]
  {:initial-state {:page :home-page}
   :query [:page]}
  (dom/div
   #js {:onClick #(navigation/navigate {:handler :user-page
                                        :route-params {:first-name "Keigo"}})}
   (ui-logo)))


(defsc UserPage [this {:keys [user]}]
  {:initial-state {:page :user-page}
   :query [:page {:user (get-query User)}]
   :componentDidMount #(data.fetch/load this :user User {:params (navigation/route-params)
                                                         :target [:user-page :page :user]})}
  (if (empty? user)
    (ui-logo)
    (ui-user user)))


(defsc UnknownPage [this {:keys []}]
  {:initial-state {:page :unknown-page}
   :query [:page]}
  (dom/div
   nil
   (dom/div
    #js {:className (u/bem [:text :heading-huge :font-weight-bold :colour-grey-medium :align-center])}
    ":(")
   (dom/div
    #js {:className (u/bem [:box :margin-medium])})
   (dom/div
    #js {:className (u/bem [:text :heading-medium :font-weight-bold :colour-black-light])}
    "You're lost")))


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
