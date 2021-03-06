(ns user
  (:require [app.operations :as operations]
            [app.server :as server]
            [clojure.tools.namespace.repl :as repl]
            [com.stuartsierra.component :as component]
            [datomic.api :as datomic]))

(repl/set-refresh-dirs "src/dev" "src/main")

(def system (atom nil))

(declare reset)

(defn refresh
  "A function for refreshing live code. Use this if the server is stopped
   (e.g. you used `reset` but there was a compiler error). Otherwise, use `reset`."
  [& args]
  (if @system
    (println "The server is running. Use `reset` instead.")
    (apply repl/refresh args)))

(defn stop
  "A function for stopping the currently running server."
  []
  (when @system
    (swap! system component/stop))
  (reset! system nil))

(defn go
  "A function for starting the server."
  ([]
   (if @system
     (println "The server is already running. Use reset to stop, refresh, and start.")
     (do
       (when-let [new-system (server/make-system "config/dev.edn")]
         (reset! system new-system))
       (swap! system component/start)))))

(defn reset
  "A function for stopping the server, refresh the code, and restart the server."
  []
  (stop)
  (refresh :after 'user/go))


(comment

  (require 'datomic.api)

  (def db-uri "datomic:dev://localhost:4334/core")

  (def conn (datomic.api/connect db-uri))

  (datomic.api/q `[:find ?e :where [?e :user/first-name "Keigo"]] (datomic.api/db conn))

  (datomic.api/q `[:find ?e :where [?e :user/created-at]] (datomic.api/db conn))

  (datomic.api/entity (datomic.api/db conn) 17592186045426)

  (datomic.api/pull (datomic.api/db conn) [:db/id :user/first-name :user/created-at] 17592186045426)

  )
