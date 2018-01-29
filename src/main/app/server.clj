(ns app.server
  (:require [datomic.api :as d]
            [fulcro.easy-server :as easy-server]
            [fulcro.server :as server]
            [com.stuartsierra.component :as component]))

(def db-uri "datomic:mem://core")

(def schema
  [{:db/doc "A user's first name"
    :db/ident :user/first-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A user's last name"
    :db/ident :user/last-name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A user's avatar url"
    :db/ident :user/avatar-url
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A user's calendars"
    :db/ident :user/calendars
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A calendar's title"
    :db/ident :calendar/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A calendar's subtitle"
    :db/ident :calendar/subtitle
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A calendar's colour option"
    :db/ident :calendar/colour-option
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}
   {:db/doc "A calendar's checked dates"
    :db/ident :calendar/checked-dates
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many
    :db/id #db/id [:db.part/db]
    :db.install/_attribute :db.part/db}])

(def entities
  [{:db/id (d/tempid :db.part/user -1)
    :calendar/title "Some Title"
    :calendar/subtitle "some subtitle"
    :calendar/colour-option :a
    :calendar/checked-dates #{"20180110" "20180114" "20180122"}}
   {:db/id (d/tempid :db.part/user -2)
    :calendar/title "Another Title"
    :calendar/subtitle "some other subtitle"
    :calendar/colour-option :b
    :calendar/checked-dates #{"20180101" "20180114" "20180122"}}
   {:db/id (d/tempid :db.part/user -3)
    :user/first-name "Keigo"
    :user/last-name "Suzukawa"
    :user/avatar-url "images/avatar.jpg"
    :user/calendars #{(d/tempid :db.part/user -1) (d/tempid :db.part/user -2)}}])


(defrecord Db [config]
  component/Lifecycle
  (start [component]
    (println "Starting database")
    (let [db-uri (get-in config [:value :db-uri])
          result (d/create-database db-uri)
          conn (d/connect db-uri)]
      @(d/transact conn schema)
      @(d/transact conn entities)
      (assoc component :conn conn)))

  (stop [component]
    (println ";; Stopping database")
    (d/delete-database (get-in config [:value :db-uri]))
    (assoc component :conn nil)))

(defn make-system [config-path]
  (easy-server/make-fulcro-server
   :config-path config-path
   :parser-injections #{:config :db}
   :components {:db (component/using (map->Db {}) [:config])}))
