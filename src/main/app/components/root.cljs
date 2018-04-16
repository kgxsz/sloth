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


(defsc HomePage [this {:keys [auth-attempt]}]
  {:initial-state (fn [_] {:page :home-page})
   :query [:page
           {:auth-attempt (get-query AuthAttempt)}]}

  (let [button-disabled (some? auth-attempt)]

    (dom/div
     #js {:className (u/bem [:page])}

     (dom/div
      #js {:className (u/bem [:page__header])})

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


(defn fetch-user [this]
  (let [{:keys [navigation]} (fulcro/props this)]
    (data.fetch/load this :user User
                     {:params (:route-params navigation)
                      :target [:user-page :page :user]
                      :marker false
                      :post-mutation `operations/process-fetched-user!})))


(defsc UserPage [this {:keys [navigation user user-fetched]}]
  {:initial-state (fn [_] {:page :user-page
                           :user-fetched false})
   :query [:page
           :user-fetched
           {[:navigation '_] [:route-params]}
           {:user (get-query User)}]
   :componentDidMount #(fetch-user this)}
  (let [me (= "me" (get-in navigation [:route-params :user-id]))]
    (dom/div
     #js {:className (u/bem [:page])}
     (dom/div
      #js {:className (u/bem [:page__header])})
     (dom/div
      #js {:className (u/bem [:page__body])}
      (cond
        (not user-fetched)
        (ui-logo)

        (and (not me) (nil? user))
        (ui-sad-message {:message "This user doesn't exist!"})

        (and me (nil? user))
        (ui-sad-message {:message "You need to sign in!"})

        :else
        (ui-user user)))
     (dom/div
      #js {:className (u/bem [:page__footer])}))))


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
  :home-page HomePage
  :auth-page AuthPage
  :user-page UserPage
  :unknown-page UnknownPage)


(def ui-pages (factory Pages))


(def routing-tree
  (routing/routing-tree
   (routing/make-route :home-page [(routing/router-instruction :pages [:home-page :page])])
   (routing/make-route :auth-page [(routing/router-instruction :pages [:auth-page :page])])
   (routing/make-route :user-page [(routing/router-instruction :pages [:user-page :page])])
   (routing/make-route :unknown-page [(routing/router-instruction :pages [:unknown-page :page])])))


(defsc Root [this {:keys [ui/react-key navigation session-user-fetched pages]}]
  {:initial-state (fn [_] (merge routing-tree {:pages (get-initial-state Pages {})
                                               :session-user-fetched false}))
   :query [:ui/react-key
           :navigation
           :session-user
           :session-user-fetched
           {:pages (get-query Pages)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (if (or (nil? navigation) (not session-user-fetched))
     (dom/div
      #js {:className (u/bem [:page])}
      (dom/div
       #js {:className (u/bem [:page__header])})
      (dom/div
       #js {:className (u/bem [:page__body])}
       (ui-logo))
      (dom/div
       #js {:className (u/bem [:page__footer])}))
     (ui-pages pages))))
