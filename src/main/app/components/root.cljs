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
           :auth-attempt/client-id
           :auth-attempt/scope]}
  (dom/div nil))

(defsc HomePage [this {:keys [auth-attempt]}]
  {:initial-state {:page :home-page}
   :query [:page {:auth-attempt (get-query AuthAttempt)}]
   :componentDidUpdate (fn [prev-props  _]
                         ;; TODO - fix this please
                         (let [{:keys [prev-auth-attempt]} prev-props
                               {:keys [auth-attempt] :as props} (fulcro/props this)
                               prev-auth-attempt-id (get-in prev-props [:auth-attempt :db/id])
                               current-auth-attempt-id (get-in (fulcro/props this) [:auth-attempt :db/id])]

                           (js/console.warn "previouse" prev-props)
                           (js/console.warn "currente" props)
                           (js/console.warn "typee" (type (:db/id auth-attempt)))
                           (js/console.warn "tempid?" (instance? fulcro.tempid/TempId (:db/id auth-attempt)))
                           (js/console.warn "int?" (int? (:db/id auth-attempt)))

                           (when (and (instance? fulcro.tempid/TempId prev-auth-attempt-id)
                                      (int? current-auth-attempt-id))
                             (data.fetch/load this :auth-attempt AuthAttempt
                                              {:params {:id current-auth-attempt-id}
                                               :target [:home-page :page :auth-attempt]}))))}
  (js/console.warn auth-attempt)
  (js/console.warn (int? (:db/id auth-attempt)))
  (dom/div
   ;; TODO - explore making the call directly here to get the goods
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
