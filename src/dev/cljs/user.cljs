(ns cljs.user
  (:require [app.components.root :refer [Root]]
            [app.client :as client]
            [fulcro.client.core :as fc]))

(defn dump
  "A function for printing the client db via the cljs repl."
  [& keys]
  (let [state-map @(om.next/app-state (-> client/app deref :reconciler))
        data-of-interest (if (seq keys)
                           (get-in state-map keys)
                           state-map)]
    data-of-interest))

(defn refresh
  "A function for Figwheel to refresh the app when files are edited."
  []
  (swap! client/app fc/mount Root "root"))

(refresh)
