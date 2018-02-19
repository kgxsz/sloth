(ns app.navigation
  (:require [bidi.bidi :as bidi]
            [fulcro.client.routing :as routing]
            [pushy.core :as pushy]
            [fulcro.client.primitives :as fulcro]))


(defonce navigation (atom nil))


(def routes ["/" [["" :home-page]
                  [["user/" :first-name] :user-page]
                  [true :unknown-page]]])


(def routing-tree
  (routing/routing-tree
   (routing/make-route :home-page [(routing/router-instruction :pages [:home-page :page])])
   (routing/make-route :user-page [(routing/router-instruction :pages [:user-page :page])])
   (routing/make-route :unknown-page [(routing/router-instruction :pages [:unknown-page :page])])))


(defn navigate [{:keys [handler route-params]}]
  (let [route-params (-> route-params vec flatten)
        path (apply bidi/path-for routes handler route-params)]
    (pushy/set-token! @navigation path)))


(defn start-navigation [reconciler]
  (reset! navigation (pushy/pushy
                      (fn [location]
                        (fulcro/transact! reconciler `[(routing/route-to ~location)]))
                      (partial bidi/match-route routes)))
  (pushy/start! @navigation))
