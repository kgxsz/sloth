(ns app.main
  (:require [app.components.root :refer [Root]]
            [app.client :as client]
            [fulcro.client :as fulcro.client]))

(reset! client/app (fulcro.client/mount @client/app Root "root"))
