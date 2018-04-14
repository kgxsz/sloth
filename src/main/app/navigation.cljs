(ns app.navigation
  (:require [bidi.bidi :as bidi]
            [cemerick.url :as url]
            [medley.core :as medley]
            [clojure.string :as string]
            [fulcro.client.routing :as routing]
            [pushy.core :as pushy]
            [fulcro.client.primitives :as fulcro]
            [fulcro.client.impl.application :as app]))


(defonce navigation (atom nil))


(def routes ["/" [["" :home-page]
                  ["auth" :auth-page]
                  [["users/" :user-id] :user-page]
                  [true :unknown-page]]])


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
                        (fulcro/transact! reconciler `[(routing/route-to ~location)
                                                       (app.operations/update-navigation! {:handler ~(:handler location)
                                                                                           :route-params ~(route-params)
                                                                                           :query-params ~(query-params)})
                                                       :ui/react-key]))
                      (partial bidi/match-route routes)))
  (pushy/start! @navigation))
