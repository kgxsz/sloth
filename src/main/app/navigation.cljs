(ns app.navigation
  (:require [bidi.bidi :as bidi]
            [cemerick.url :as url]
            [medley.core :as medley]
            [clojure.string :as string]
            [fulcro.client.routing :as routing]
            [pushy.core :as pushy]
            [fulcro.client.primitives :as fulcro]))


(defonce navigation (atom nil))


(def routes ["/" [["" :home-page]
                  ["auth" :auth-page]
                  [true :unknown-page]]])


(def routing-tree
  (routing/routing-tree
   (routing/make-route :loading-page [(routing/router-instruction :pages [:loading-page :page])])
   (routing/make-route :home-page [(routing/router-instruction :pages [:home-page :page])])
   (routing/make-route :auth-page [(routing/router-instruction :pages [:auth-page :page])])
   (routing/make-route :unknown-page [(routing/router-instruction :pages [:unknown-page :page])])))


(defn navigate-externally [{:keys [url query-params]}]
  (let [query-string (when-not (string/blank? (url/map->query query-params))
                       (str "?" (url/map->query query-params)))]
    (set! js/window.location (str url query-string))))


(defn navigate-internally [{:keys [handler route-params query-params replace?]}]
  (let [query-string (when-not (string/blank? (url/map->query query-params))
                       (str "?" (url/map->query query-params)))
        update-token! (if replace? pushy/replace-token! pushy/set-token!)
        route-params (-> route-params vec flatten)
        path (str (apply bidi/path-for routes handler route-params) query-string)]
    (update-token! @navigation path)))


(defn route-params []
  (:route-params (bidi/match-route routes (pushy/get-token @navigation))))


(defn query-params []
  (->> (string/split (pushy/get-token @navigation) "?")
       (second)
       (url/query->map)
       (medley/map-keys keyword)))


(defn start-navigation [reconciler]
  (reset! navigation (pushy/pushy
                      (fn [location]
                        (fulcro/transact! reconciler `[(routing/route-to ~location) :ui/react-key]))
                      (partial bidi/match-route routes)))
  (pushy/start! @navigation))
