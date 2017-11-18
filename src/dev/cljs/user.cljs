(ns cljs.user
  (:require [app.ui :as ui]
            [app.core :as core]
            [fulcro.client.core :as fc]))

(defn dump
  "Convenience function for printing the client db via the cljs repl."
  [& keys]
  (let [state-map @(om.next/app-state (-> core/app deref :reconciler))
        data-of-interest (if (seq keys)
                           (get-in state-map keys)
                           state-map)]
    data-of-interest))

(defn refresh
  "Convenience function for Figwheel to remount the app when files are edited."
  []
  (swap! core/app fc/mount ui/App "app"))

(refresh)


