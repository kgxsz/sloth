(ns app.client
  (:require [app.navigation :as navigation]
            [fulcro.client :as fulcro.client]
            [fulcro.client.data-fetch :as data.fetch]))


(defonce app (atom (fulcro.client/new-fulcro-client
                    :started-callback (fn [{:keys [reconciler] :as app}]
                                        (data.fetch/load app :session-user
                                                         app.components.user/User
                                                         {:marker false
                                                          :post-mutation `app.operations/process-fetched-session-user!})
                                        (navigation/start-navigation reconciler)))))
