(ns app.components.root
  (:require [app.components.logo :refer [ui-logo]]
            [app.components.user :refer [ui-user User]]
            [app.navigation :as navigation]
            [app.operations :as operations]
            [app.utils :as u]
            [fulcro.client.data-fetch :as data.fetch]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc get-query get-initial-state factory]]
            [fulcro.client.routing :refer-macros [defrouter]]
            [fulcro.client.primitives :as fulcro]))


;; TODO - find a home for this guy
(defsc AuthAttempt [this {:keys [db/id ui/fetch-state] :auth-attempt/keys [client-id redirect-url scope]}]
  {:ident [:auth-attempt/by-id :db/id]
   :initial-state (fn [_] {:id '_})
   :query [:db/id
           :auth-attempt/client-id
           :auth-attempt/redirect-url
           :auth-attempt/scope
           :ui/fetch-state]
   :componentDidMount (fn []
                        ;; TODO - put this in a transaction
                         (let [{:keys [db/id] :as auth-attempt} (fulcro/props this)]
                           (when (some? id)
                             (navigation/navigate-externally
                              {:url "https://www.facebook.com/v2.9/dialog/oauth"
                               :query-params {:client_id (:auth-attempt/client-id auth-attempt)
                                              :state id
                                              :redirect_uri (:auth-attempt/redirect-url auth-attempt)
                                              :scope (:auth-attempt/scope auth-attempt)}}))))}
  (cond
    id (dom/button
        #js {:disabled true}
        "authed")
    fetch-state (dom/button
                 #js {:disabled true}
                 "authing")
    :else (dom/button
           #js {:onClick #(fulcro/transact! this `[(operations/initialise-auth-attempt!)])}
           "auth")))


(def ui-auth-attempt (factory AuthAttempt {:keyfn :db/id}))


(defsc HomePage [this {:keys [auth-attempt]}]
  {:initial-state (fn [_] {:page :home-page :auth-attempt (get-initial-state AuthAttempt {})})
   :query [:page {:auth-attempt (get-query AuthAttempt)}]}
  (dom/div
   nil
   (ui-logo)
   (ui-auth-attempt auth-attempt)))


(defsc AuthPage [this {:keys []}]
  {:initial-state {:page :auth-page}
   :query [:page]}
  (dom/div
   nil
   (ui-logo)
   "auth!"))


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
  :auth-page AuthPage
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
