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
    (log/info "starting database." (str "datomic:sql://core?jdbc:" (get-in config [:value :jdbc-url])))
    (let [db-uri (str "datomic:sql://core?jdbc:" (get-in config [:value :jdbc-url]))
          conn (do (d/create-database db-uri)
                   (d/connect db-uri))
          migrations [:sloth/user-schema
                      :sloth/calendar-schema
                      :sloth/entities]]
      (c/ensure-conforms conn (c/read-resource "migrations.edn") migrations)
      (assoc component :conn conn)))
  (stop [component]
    (log/info "stopping database.")
    (assoc component :conn nil)))

(defn make-system [config-path]
  (easy-server/make-fulcro-server
   :config-path config-path
   :parser-injections #{:config :db}
   :components {:db (component/using (map->Db {}) [:config])}))
