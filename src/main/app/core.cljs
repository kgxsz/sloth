(ns app.core
  (:require [fulcro.client.core :as fc]
            [fulcro.client.data-fetch :as df]
            [app.ui :as ui]))

(defonce app (atom (fc/new-fulcro-client
                    :started-callback (fn [app]
                                        (df/load app :current-user ui/User)))))


