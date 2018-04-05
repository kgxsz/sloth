(ns app.components.root
  (:require [app.components.logo :refer [ui-logo]]
            [app.components.notification :refer [ui-notification]]
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


(defn invitation-code-invalid? []
  (let [{:keys [invitation-code]} (navigation/query-params)]
    (not= "188923" invitation-code)))


(defn page-ready? [data-fetch-dispatched session-user]
  (and (true? data-fetch-dispatched)
       (nil? (:ui/fetch-state session-user))))


(defsc HomePage [this {:keys [data-fetch-dispatched auth-attempt session-user]}]
  {:initial-state (fn [_] {:page :home-page
                           :data-fetch-dispatched false
                           :page-initialised false})
   :query [:page
           :data-fetch-dispatched
           {:auth-attempt (get-query AuthAttempt)}
           {:session-user (get-query User)}]
   :componentDidMount #(fetch-session-user this)}

  (if-not (page-ready? data-fetch-dispatched session-user)
    (dom/div
     #js {:className (u/bem [:page])}
     (dom/div
      #js {:className (u/bem [:page__content])}
      (ui-logo)))

    (dom/div
     #js {:className (u/bem [:page])}
     (if (some? session-user)
       (dom/div
        #js {:className (u/bem [:page__content])}
        (ui-user session-user))

       (dom/div
        #js {:className (u/bem [:page__content])}
        (ui-logo)

        (if (invitation-code-invalid?)
          (ui-notification {:title "Warning"
                            :paragraph "You need an invitation code to proceed."})
          (dom/button
           #js {:className (u/bem [:button])
                :onClick #(initialise-auth-attempt this)
                :disabled (some? auth-attempt)}
           (dom/div
            #js {:className (u/bem [:text :colour-blue-dark])}
            "Sign in with Facebook")
           (dom/div
            #js {:className (u/bem [:icon :facebook :colour-blue-dark])}))))))))


(defn finalise-auth-attempt [this]
  (let [{:keys [code state error]} (navigation/query-params)]
    (when-not error
      (data.fetch/load this :finalised-auth-attempt AuthAttempt
                       {:params {:auth-attempt-id (js/parseInt state)
                                 :code code}
                        :target [:auth-page :page :auth-attempt]
                        :post-mutation `operations/process-finalised-auth-attempt!}))))


(defn auth-attempt-failed? [auth-attempt]
  (or (:error (navigation/query-params))
      (:auth-attempt/failed-at auth-attempt)))


(defsc AuthPage [this {:keys [auth-attempt]}]
  {:initial-state (fn [_] {:page :auth-page})
   :query [:page {:auth-attempt (get-query AuthAttempt)}]
   :componentDidMount #(finalise-auth-attempt this)}

  (if (auth-attempt-failed? auth-attempt)

    (dom/div
     #js {:className (u/bem [:page])}
     (dom/div
      #js {:className (u/bem [:page__content])}
      (dom/div
       #js {:className (u/bem [:text :heading-huge :font-weight-bold :colour-grey-medium :align-center])}
       ":(")
      (dom/div
       #js {:className (u/bem [:box :margin-medium])})
      (dom/div
       #js {:className (u/bem [:text :heading-medium :font-weight-bold :colour-black-light])}
       "Something went wrong!")))

    (dom/div
     #js {:className (u/bem [:page])}
     (dom/div
      #js {:className (u/bem [:page__content])}
      (ui-logo)))))


(defsc UnknownPage [this {:keys []}]
  {:initial-state (fn [_] {:page :unknown-page})
   :query [:page]}
  (dom/div
   #js {:className (u/bem [:page])}
   (dom/div
    #js {:className (u/bem [:page__content])}
    (dom/div
     #js {:className (u/bem [:text :heading-huge :font-weight-bold :colour-grey-medium :align-center])}
     ":(")
    (dom/div
     #js {:className (u/bem [:box :margin-medium])})
    (dom/div
     #js {:className (u/bem [:text :heading-medium :font-weight-bold :colour-black-light])}
     "You're lost!"))))


(defrouter Pages :pages
  (ident [this props] [(:page props) :page])
  :home-page HomePage
  :auth-page AuthPage
  :unknown-page UnknownPage)


(def ui-pages (factory Pages))


(defsc Root [this {:keys [ui/react-key pages]}]
  {:initial-state (fn [_] (merge navigation/routing-tree {:pages (get-initial-state Pages {})}))
   :query [:ui/react-key
           {:pages (get-query Pages)}]}
  (dom/div
   #js {:key react-key
        :className (u/bem [:app])}
   (ui-pages pages)))
