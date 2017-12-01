(ns app.core
  (:require [app.components.user :refer [User]]
            [fulcro.client.core :as fc]
            [fulcro.client.data-fetch :as df]))

(defonce app (atom (fc/new-fulcro-client
                    :started-callback (fn [app]
                                        (df/load app :current-user User)))))


