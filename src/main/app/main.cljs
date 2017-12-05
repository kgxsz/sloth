(ns app.main
  (:require [app.components.root :refer [Root]]
            [app.client :as client]
            [fulcro.client.core :as fc]))

(reset! client/app (fc/mount @client/app Root "root"))
