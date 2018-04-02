(ns app.server
  (:require [aero.core :as aero]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [datomic.api :as datomic]
            [fulcro.server :as fulcro.server]
            [io.rkn.conformity :as conformity]
            [org.httpkit.server :as http.server]
            [ring.middleware.content-type :as middleware.content-type]
            [ring.middleware.gzip :as middleware.gzip]
            [ring.middleware.not-modified :as middleware.not-modified]
            [ring.middleware.resource :as middleware.resource]
            [ring.middleware.session :as middleware.session]
            [taoensso.timbre :as log]))


(defrecord Config []
  component/Lifecycle
  (start [component]
    (try
      (log/info "starting config")
      (let [config (aero/read-config "resources/config.edn")]
        (merge component config))

      (catch Exception e
        (log/error "unable to start config")
        (throw e))))

  (stop [component]
    (log/info "stopping config")
    component))


(defrecord HttpServer []
  component/Lifecycle
  (start [{:keys [db config] :as component}]
    (try
      (log/info "starting http-server")
      (let [session-config {:cookie-name (get-in config [:session-cookie :name])
                            :cookie-attrs {:max-age (edn/read-string (get-in config [:session-cookie :max-age]))
                                           :secure (edn/read-string (get-in config [:session-cookie :secure]))
                                           :http-only (edn/read-string (get-in config [:session-cookie :http-only]))
                                           :same-site (edn/read-string (get-in config [:session-cookie :same-site]))}}
            default-handler (fn [req]
                              {:status 200
                               :headers {"Content-Type" "text/html"}
                               :body (io/file "resources/public/index.html")})
            wrap-api (fn [handler config db]
                       (fn [request]
                         (if (= "/api" (:uri request))
                           (fulcro.server/handle-api-request
                            (fulcro.server/fulcro-parser)
                            {:config config :db db :session (:session request)}
                            (:transit-params request))
                           (handler request))))
            ring-stack (-> default-handler
                           (wrap-api config db)
                           (middleware.session/wrap-session session-config)
                           (fulcro.server/wrap-transit-params)
                           (fulcro.server/wrap-transit-response)
                           (middleware.resource/wrap-resource "public")
                           (middleware.content-type/wrap-content-type)
                           (middleware.not-modified/wrap-not-modified)
                           (middleware.gzip/wrap-gzip))
            stop-http-server (http.server/run-server ring-stack {:port (edn/read-string (get-in config [:port]))})]
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
      (let [db-uri (get-in config [:db-uri])
            conn (do (datomic/create-database db-uri)
                     (datomic/connect db-uri))
            migrations [:sloth/calendar-schema-250318
                        :sloth/user-schema-250318
                        :sloth/auth-attempt-schema-250318]]
        (conformity/ensure-conforms conn (conformity/read-resource "migrations.edn") migrations)
        (assoc component :conn conn))

      (catch Exception e
        (log/error "unable to start db")
        (throw e))))

  (stop [component]
    (log/info "stopping db")
    (assoc component :conn nil)))


(defn make-system [profile]
  (component/system-map
   :config (component/using (map->Config {}) [])
   :db (component/using (map->Db {}) [:config])
   :http-server (component/using (map->HttpServer {}) [:config :db])))
