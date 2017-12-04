(ns app.server
  (:require [fulcro.easy-server :as easy-server]
            [fulcro.server :as server]))

(defn make-system [config-path]
  (easy-server/make-fulcro-server
   :config-path config-path
   :parser (server/fulcro-parser)))
