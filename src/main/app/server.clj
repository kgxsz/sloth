(ns app.server
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [fulcro.easy-server :as easy-server]
            [fulcro.server :as server]
            [io.rkn.conformity :as c]
            [taoensso.timbre :as log]

            ;; TODO - clean this up
            [clojure.java.io :as io]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as rsp :refer [response file-response resource-response]]
            [org.httpkit.server :as http.server]
            [hiccup.page :as page]))


(defn default-handler []
  (fn [req]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body (io/file (io/resource "public/index.html"))}))


(def parser (server/fulcro-parser))


(defn wrap-api [handler env]
  (fn [request]
    (if (= "/api" (:uri request))
      (server/handle-api-request parser
                                 env
                                 (:transit-params request))
      (handler request))))


(defrecord HttpServer []
  component/Lifecycle
  (start [{:keys [db config] :as component}]
    (try
      (log/info "starting http-server")
      (let [port       (get-in config [:value :port])
            ring-stack (-> (default-handler)
                           (wrap-api {:db db :config config})
                           (server/wrap-transit-params)
                           (server/wrap-transit-response)
                           (wrap-resource "public")
                           (wrap-content-type)
                           (wrap-not-modified)
                           (wrap-gzip))
            stop-http-server (http.server/run-server ring-stack {:port port})]
        (assoc component :stop-http-server stop-http-server))

      (catch Exception e
        (log/error "unable to start http-server")
        (throw e))))

  (stop [component]
    (log/info "stopping http-server")
    (when-let [stop-http-server (:stop-http-server component)]
      (stop-http-server))
    (assoc component :stop-http-server nil)))


(defrecord Db []
  component/Lifecycle
  (start [{:keys [config] :as component}]
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
  (component/system-map
   :config (server/new-config config-path)
   :db (component/using (map->Db {}) [:config])
   :http-server (component/using (map->HttpServer {}) [:config :db])))

