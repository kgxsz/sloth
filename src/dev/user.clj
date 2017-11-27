(ns user
  (:require [clojure.tools.namespace.repl :as repl]
            [server.core :as server]
            [server.operations :as operations]
            [com.stuartsierra.component :as component]))

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
