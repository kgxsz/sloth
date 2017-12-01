(ns app.main
  (:require [app.components.root :refer [Root]]
            [app.core :as core]
            [fulcro.client.core :as fc]))

(reset! core/app (fc/mount @core/app Root "root"))
