(ns app.client
  (:require [app.components.user :refer [User]]
            [app.navigation :as navigation]
            [fulcro.client :as fc]
            [fulcro.client.data-fetch :as df]))


(defonce app (atom (fc/new-fulcro-client
                    :started-callback (fn [{:keys [reconciler] :as app}]
                                        (navigation/start-navigation reconciler)
                                        (df/load app :current-user User)))))

