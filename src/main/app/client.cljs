(ns app.client
  (:require [app.components.user :refer [User]]
            [app.navigation :as navigation]
            [fulcro.client :as client]))


(defonce app (atom (client/new-fulcro-client
                    :started-callback (fn [{:keys [reconciler] :as app}]
                                        (navigation/start-navigation reconciler)))))

