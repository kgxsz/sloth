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


(defsc AuthAttempt [this _]
  {:ident [:auth-attempt/by-id :db/id]
   :query [:db/id
           :auth-attempt/client-id
           :auth-attempt/redirect-url
           :auth-attempt/scope
           :ui/fetch-state]})

(defn initialise-auth-attempt [this]
  (data.fetch/load this :initialised-auth-attempt AuthAttempt
                   {:target [:home-page :page :initialised-auth-attempt]
                    :post-mutation `operations/process-initialised-auth-attempt!}))

(defn auth-attempt-initialising? [auth-attempt]
  (some? (:ui/fetch-state auth-attempt)))

(defn auth-attempt-initialised? [auth-attempt]
  (some? (:db/id auth-attempt)))

(defn cannot-initialise-auth-attempt? [auth-attempt]
  (or (auth-attempt-initialising? auth-attempt)
      (auth-attempt-initialised? auth-attempt)))

(defsc HomePage [this {:keys [auth-attempt]}]
  {:initial-state {:page :home-page}
   :query [:page {:auth-attempt (get-query AuthAttempt)}]}
  (dom/div
   nil
   (ui-logo)
   (dom/button
    #js {:onClick #(initialise-auth-attempt this)
         :disabled (cannot-initialise-auth-attempt? auth-attempt)}
    (cond
      (auth-attempt-initialised? auth-attempt) "authed"
      (auth-attempt-initialising? auth-attempt) "authing"
      :else "auth"))))


(defsc AuthPage [this {:keys []}]
  {:initial-state {:page :auth-page}
   :query [:page]
   :componentDidMount (fn []
                        (data.fetch/load this :finalised-auth-attempt AuthAttempt
                                         {:params (navigation/query-params)
                                          :target [:auth-page :page :finalised-auth-attempt]
                                          :post-mutation `operations/process-finalise-auth-attempt!}))}
  (ui-logo))


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
