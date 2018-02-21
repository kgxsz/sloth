(ns app.server
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [fulcro.easy-server :as easy-server]
            [fulcro.server :as server]
            [io.rkn.conformity :as c]
            [taoensso.timbre :as log]

            ;; TODO - clean this up
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as rsp :refer [response file-response resource-response]]
            [org.httpkit.server]
            [hiccup.page :as page]
            ))


(defn not-found-handler []
  (fn [req]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    (slurp "resources/public/index.html")}))

(def parser (server/fulcro-parser))

(defn wrap-api [handler uri]
  (fn [request]
    (if (= uri (:uri request))
      (server/handle-api-request parser {} (:transit-params request))
      (handler request))))

(defn my-tiny-server []
  (let [port       9002
        ring-stack (-> (not-found-handler)
                       (wrap-api "/api")
                       (server/wrap-transit-params)
                       (server/wrap-transit-response)
                       (wrap-resource "public")
                       (wrap-content-type)
                       (wrap-not-modified))
        (wrap-gzip)
        (org.httpkit.server/run-server ring-stack {:port port})]))









(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (try
      (log/info "starting db")
      (let [db-uri (get-in config [:value :db-uri])
            conn (do (d/create-database db-uri)
                     (d/connect db-uri))
            migrations [:sloth/user-schema
                        :sloth/calendar-schema
                        :sloth/entities]]
        (c/ensure-conforms conn (c/read-resource "migrations.edn") migrations)
        (assoc component :conn conn))

      (catch Exception e
        (log/error "unable to start db")
        (throw e))))

  (stop [component]
    (log/info "stopping db")
    (assoc component :conn nil)))

(defn make-system [config-path]
  (easy-server/make-fulcro-server
   :config-path config-path
   :parser-injections #{:config :db}
   :components {:db (component/using (map->Db {}) [:config])}))

