(ns app.operations
  (:require [app.navigation :as navigation]
            [fulcro.client.data-fetch :as data.fetch]
            [fulcro.client.mutations :refer [defmutation]]))


(defmutation process-initialised-auth-attempt!
  [_]
  (action [{:keys [state]}]
          (let [state @state
                ident (get-in state [:home-page :page :auth-attempt])
                auth-attempt (get-in state ident)]
            (navigation/navigate-externally
             {:url "https://www.facebook.com/v2.9/dialog/oauth"
              :query-params {:client_id (:auth-attempt/client-id auth-attempt)
                             :state (:db/id auth-attempt)
                             :redirect_uri (:auth-attempt/redirect-url auth-attempt)
                             :scope (:auth-attempt/scope auth-attempt)}}))))


(defmutation process-finalised-auth-attempt!
  [_]
  (action [{:keys [state]}]
          (let [state @state
                auth-attempt-ident (get-in state [:auth-page :page :auth-attempt])
                auth-attempt (get-in state auth-attempt-ident)]
            (when-not (:auth-attempt/failed-at auth-attempt)
              (navigation/navigate-internally
               {:handler :user-page
                :route-params {:user-id "me"}})))))


(defmutation process-fetched-user!
  [_]
  (action [{:keys [state]}]
          (swap! state assoc-in [:user-page :page :user-fetched] true)))


(defmutation update-navigation!
  [{:keys [handler route-params query-params]}]
  (action [{:keys [state]}]
          (swap! state assoc :navigation {:handler handler
                                          :route-params route-params
                                          :query-params query-params})))


(defmutation add-checked-date!
  [{:keys [calendar-id date]}]
  (action [{:keys [state]}]
          (swap! state
                 update-in
                 [:calendar/by-id calendar-id :calendar/checked-dates]
                 #(-> % set (conj date) vec)))
  (remote [env] true))


(defmutation remove-checked-date!
  [{:keys [calendar-id date]}]
  (action [{:keys [state]}]
          (swap! state
                 update-in
                 [:calendar/by-id calendar-id :calendar/checked-dates]
                 #(-> % set (disj date) vec)))
  (remote [env] true))
