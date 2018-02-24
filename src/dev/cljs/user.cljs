(ns cljs.user
  (:require [app.components.root :refer [Root]]
            [app.client :as client]
            [fulcro.client :as fc]))


(defn dump
  "A function for printing the client db via the cljs repl."
  [& keys]
  (let [state-map (-> client/app deref :reconciler deref)
        data-of-interest (if (seq keys)
                           (get-in state-map keys)
                           state-map)]
    data-of-interest))


(defn mount []
  (reset! client/app (fc/mount @client/app Root "root")))


(mount)
