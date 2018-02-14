(ns app.client
  (:require [app.components.user :refer [User]]
            [fulcro.client :as fc]
            [fulcro.client.data-fetch :as df]))

(defonce app (atom (fc/new-fulcro-client
                    :started-callback (fn [app]
                                        (df/load app :current-user User)))))
