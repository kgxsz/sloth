(ns server.core
  (:require [fulcro.easy-server :as easy]
            [fulcro.server :as server]
            [taoensso.timbre :as timbre]))

(defn make-system [config-path]
  (easy/make-fulcro-server
   :config-path config-path
   :parser (server/fulcro-parser)))
