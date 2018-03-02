(ns app.server
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [datomic.api :as datomic]
            [fulcro.server :as fulcro.server]
            [io.rkn.conformity :as conformity]
            [org.httpkit.server :as http.server]
            [ring.middleware.content-type :as middleware.content-type]
            [ring.middleware.gzip :as middleware.gzip]
            [ring.middleware.not-modified :as middleware.not-modified]
            [ring.middleware.resource :as middleware.resource]
            [taoensso.timbre :as log]))


(defn default-handler []
  (fn [req]
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (io/file "resources/public/index.html")}))


(def parser (fulcro.server/fulcro-parser))


(defn wrap-api [handler env]
  (fn [request]
    (if (= "/api" (:uri request))
      (fulcro.server/handle-api-request
       parser
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
                           (fulcro.server/wrap-transit-params)
                           (fulcro.server/wrap-transit-response)
                           (middleware.resource/wrap-resource "public")
                           (middleware.content-type/wrap-content-type)
                           (middleware.not-modified/wrap-not-modified)
                           (middleware.gzip/wrap-gzip))
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
            conn (do (datomic/create-database db-uri)
                     (datomic/connect db-uri))
            migrations [:sloth/user-schema
                        :sloth/calendar-schema
                        :sloth/entities]]
        (conformity/ensure-conforms conn (conformity/read-resource "migrations.edn") migrations)
        (assoc component :conn conn))

      (catch Exception e
        (log/error "unable to start db")
        (throw e))))

  (stop [component]
    (log/info "stopping db")
    (assoc component :conn nil)))


(defn make-system [config-path]
  (component/system-map
   :config (fulcro.server/new-config config-path)
   :db (component/using (map->Db {}) [:config])
   :http-server (component/using (map->HttpServer {}) [:config :db])))

