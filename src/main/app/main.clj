(ns app.main
  (:gen-class)
  (:require [app.operations :as operations]
            [app.server :as server]
            [com.stuartsierra.component :as component]))

(defn -main [& args]
  (let [system (server/make-system "config/prod.edn")]
    (component/start system)))
