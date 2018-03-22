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


(defsc AuthAttempt [this {:keys []}]
  {:ident [:auth-attempt/by-id :db/id]
   :query [:db/id
           :auth-attempt/scope]}
  (dom/div nil))

(defsc HomePage [this {:keys [auth-attempt-id]}]
  {:initial-state {:page :home-page}
   :query [:page
           :auth-attempt-id]
   :componentDidUpdate (fn [{:keys [auth-attempt-id]} _]
                         ;; TODO - fix this please
                         (when (and (not (int? auth-attempt-id))
                                    (int? (:auth-attempt-id (fulcro/props this))))
                           (data.fetch/load this :auth-attempt AuthAttempt
                                            {:params (select-keys (fulcro/props this) [:auth-attempt-id])
                                             :target [:home-page :page :auth-attempt]})))}
  (js/console.warn auth-attempt-id)
  (js/console.warn (int? auth-attempt-id))
  (dom/div
   #js {:onClick #(fulcro/transact! this `[(operations/initialise-auth-attempt! {:tempid ~(fulcro/tempid)})])}
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
