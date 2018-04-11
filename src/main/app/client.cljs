(ns app.client
  (:require [app.components.user :refer [User]]
            [app.navigation :as navigation]
            [fulcro.client :as fulcro.client]))


(defonce app (atom (fulcro.client/new-fulcro-client
                    :started-callback (fn [{:keys [reconciler] :as app}]
                                        ;; TODO - tie this into the process to avoid race-condition
                                        (navigation/start-navigation reconciler)))))
