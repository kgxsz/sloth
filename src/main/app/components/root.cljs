(ns app.components.root
  (:require [app.components.logo :refer [ui-logo]]
            [app.components.notification :refer [ui-notification]]
            [app.components.sad-message :refer [ui-sad-message]]
            [app.components.user :refer [ui-user User]]
            [app.operations :as operations]
            [app.utils :as u]
            [fulcro.client.data-fetch :as data.fetch]
            [fulcro.client.dom :as dom]
            [fulcro.client.primitives :refer [defsc get-query get-initial-state factory]]
            [fulcro.client.routing :as routing :refer-macros [defrouter]]
            [fulcro.client.primitives :as fulcro]))


(defsc AuthAttempt [this _]
  {:ident [:auth-attempt/by-id :db/id]
   :query [:db/id
           :auth-attempt/client-id
           :auth-attempt/redirect-url
           :auth-attempt/scope
           :auth-attempt/initialised-at
           :auth-attempt/failed-at
           :auth-attempt/succeeded-at
           :ui/fetch-state]})


(defn initialise-auth-attempt [this]
  (data.fetch/load this :initialised-auth-attempt AuthAttempt
                   {:target [:home-page :page :auth-attempt]
                    :post-mutation `operations/process-initialised-auth-attempt!}))


(defn fetch-session-user [this]
  (data.fetch/load this :session-user User
                   {:target [:home-page :page :session-user]
                    :post-mutation `operations/process-fetched-session-user!}))


(defn invitation-code-invalid? [{:keys [invitation-code]}]
  (not= "04031986" invitation-code))


(defn page-ready? [page-initialisation session-user]
  (and (:session-user-fetched page-initialisation)
       (nil? (:ui/fetch-state session-user))))


(defsc HomePage [this {:keys [page-initialisation navigation auth-attempt session-user]}]
  {:initial-state (fn [_] {:page :home-page
                           :page-initialisation {:session-user-fetched false}})
   :query [:page
           {:page-initialisation [:session-user-fetched]}
           [:navigation '_]
           {:session-user (get-query User)}
           {:auth-attempt (get-query AuthAttempt)}]
   :componentDidMount #(fetch-session-user this)}

  (let [{:keys [query-params]} navigation
        page-ready (page-ready? page-initialisation session-user)
        signed-in (and page-ready (some? session-user))
        invitation-code-invalid (invitation-code-invalid? query-params)
        show-notification (and page-ready invitation-code-invalid (not signed-in))
        button-disabled (or (some? auth-attempt) invitation-code-invalid)]

    (dom/div
     #js {:className (u/bem [:page])}

     (dom/div
      #js {:className (u/bem [:page__header])}
      (when show-notification
        (ui-notification {:title "Warning"
                          :paragraph "You need an invitation code to proceed."})))

     (cond

       (not page-ready)
       (dom/div
        #js {:className (u/bem [:page__body])}
        (ui-logo))

       (not signed-in)
       (dom/div
        #js {:className (u/bem [:page__body])}
        (ui-logo)
        (dom/button
         #js {:className (u/bem [:button
                                 :background-color-blue-medium
                                 :border-color-blue-dark
                                 :margin-top-xx-large
                                 (when button-disabled :disabled)])
              :onClick #(initialise-auth-attempt this)
              :disabled button-disabled}
         (dom/div
          #js {:className (u/bem [:text :colour-blue-dark])}
          "Sign in with Facebook")
         (dom/div
          #js {:className (u/bem [:icon :facebook :colour-blue-dark])})))

       signed-in
       (dom/div
        #js {:className (u/bem [:page__body])}
        (ui-user session-user))

       :else
       (dom/div
        #js {:className (u/bem [:page__body])}
        (ui-sad-message {:message "Something isn't right!"})))

     (dom/div
      #js {:className (u/bem [:page__footer])}))))


(defn finalise-auth-attempt [this]
  (let [{:keys [code state error]} (get-in (fulcro/props this) [:navigation :query-params])]
    (when (and (nil? error) (some? code) (some? state))
      (data.fetch/load this :finalised-auth-attempt AuthAttempt
                       {:params {:auth-attempt-id (js/parseInt state)
                                 :code code}
                        :target [:auth-page :page :auth-attempt]
                        :post-mutation `operations/process-finalised-auth-attempt!}))))


(defn auth-attempt-failed? [auth-attempt {:keys [code state error]}]
  (or (some? error) (nil? state) (nil? code)
      (:auth-attempt/failed-at auth-attempt)))


(defsc AuthPage [this {:keys [navigation auth-attempt]}]
  {:initial-state (fn [_] {:page :auth-page})
   :query [:page
           [:navigation '_]
           {:auth-attempt (get-query AuthAttempt)}]
   :componentDidMount #(finalise-auth-attempt this)}
  (dom/div
   #js {:className (u/bem [:page])}
   (dom/div
    #js {:className (u/bem [:page__header])})
   (if (auth-attempt-failed? auth-attempt (:query-params navigation))
     (dom/div
      #js {:className (u/bem [:page__body])}
      (ui-sad-message {:message "Sign in failed!"}))
     (dom/div
      #js {:className (u/bem [:page__body])}
      (ui-logo)))
   (dom/div
    #js {:className (u/bem [:page__footer])})))


(defsc UnknownPage [this _]
  {:initial-state (fn [_] {:page :unknown-page})
   :query [:page]}
  (dom/div
   #js {:className (u/bem [:page])}
   (dom/div
    #js {:className (u/bem [:page__header])})
   (dom/div
    #js {:className (u/bem [:page__body])}
    (ui-sad-message {:message "You're lost!"}))
   (dom/div
    #js {:className (u/bem [:page__footer])})))


(defrouter Pages :pages
  (ident [this props] [(:page props) :page])
  :loading-page LoadingPage
  :home-page HomePage
  :auth-page AuthPage
  :unknown-page UnknownPage)


(def ui-pages (factory Pages))


(def routing-tree
  (routing/routing-tree
   (routing/make-route :loading-page [(routing/router-instruction :pages [:loading-page :page])])
   (routing/make-route :home-page [(routing/router-instruction :pages [:home-page :page])])
   (routing/make-route :auth-page [(routing/router-instruction :pages [:auth-page :page])])
   (routing/make-route :unknown-page [(routing/router-instruction :pages [:unknown-page :page])])))


(defsc Root [this {:keys [ui/react-key navigation pages]}]
  {:initial-state (fn [_] (merge routing-tree {:pages (get-initial-state Pages {})}))
   :query [:ui/react-key
           :navigation
           {:pages (get-query Pages)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (when (some? navigation)
     (ui-pages pages))))
