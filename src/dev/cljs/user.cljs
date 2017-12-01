(ns cljs.user
  (:require [app.components.root :refer [Root]]
            [app.core :as core]
            [fulcro.client.core :as fc]))

(defn dump
  "A function for printing the client db via the cljs repl."
  [& keys]
  (let [state-map @(om.next/app-state (-> core/app deref :reconciler))
        data-of-interest (if (seq keys)
                           (get-in state-map keys)
                           state-map)]
    data-of-interest))

(defn refresh
  "A function for Figwheel to refresh the app when files are edited."
  []
  (swap! core/app fc/mount Root "root"))

(refresh)
