(ns app.server
  (:require [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [fulcro.easy-server :as easy-server]
            [fulcro.server :as server]
            [io.rkn.conformity :as c]
            [taoensso.timbre :as log]))

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

