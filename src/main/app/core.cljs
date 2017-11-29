(ns app.core
  (:require [fulcro.client.core :as fc]
            [fulcro.client.data-fetch :as df]
            [app.ui :as ui]))

(defonce app (atom (fc/new-fulcro-client
                    :started-callback (fn [app]
                                        ;; TODO - figure out how to make these compose into a single query
                                        (df/load app :server/calendars ui/Calendar {:target [:calendars]})
                                        (df/load app :server/user ui/User {:target [:user]})))))


