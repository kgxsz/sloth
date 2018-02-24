(defproject sloth "0.1.0-SNAPSHOT"

  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [com.taoensso/timbre "4.10.0"]
                 [com.datomic/datomic-pro "0.9.5656"
                  :exclusions [com.google.guava/guava]]
                 [io.rkn/conformity "0.5.1"]
                 [org.omcljs/om "1.0.0-beta1"
                  :exclusions [cljsjs/react]]
                 [fulcrologic/fulcro "2.2.0"
                  :exclusions [org.clojure/tools.reader
                               com.taoensso/encore]]
                 [com.stuartsierra/component "0.3.2"]
                 [http-kit "2.2.0"]
                 [garden "1.3.3"]
                 [bidi "2.1.3"]
                 [ring/ring-core "1.6.3"]
                 [hiccup "1.0.5"]
                 [kibu/pushy "0.3.8"]
                 [com.powernoodle/normalize "7.0.0"]
                 [clj-time "0.14.2"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [com.amazonaws/aws-java-sdk-dynamodb "1.11.273"]]

  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :username [:gpg :env/datomic-license-user]
                                   :password [:gpg :env/datomic-license-password]}}

  :source-paths ["src/main"]

  :resource-paths ["resources"]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "resources/public/css"
                                    "target"
                                    ".nrepl-port"
                                    ".lein-repl-history"]

  :omit-source true

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-garden "0.3.0"]]

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                                  [com.cemerick/piggieback "0.2.2"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [org.clojure/tools.nrepl "0.2.13"]]

                   :source-paths ["src/main" "src/dev"]

                   :prep-tasks [["garden" "once"]]

                   :figwheel {:css-dirs ["resources/public/css"]
                              :server-logfile "target/figwheel_temp/logs/figwheel_server.log"}


                   :cljsbuild {:builds [{:id "dev"
                                         :source-paths ["src/main" "src/dev"]
                                         :figwheel {:on-jsload "cljs.user/mount"}
                                         :compiler {:main cljs.user
                                                    :output-to "resources/public/js/app.js"
                                                    :output-dir "resources/public/js/app"
                                                    :preloads [devtools.preload]
                                                    :asset-path "/js/app"
                                                    :optimizations :none}}]}

                   :garden {:builds [{:source-paths ["src/main"]
                                      :stylesheet styles.core/app
                                      :compiler {:output-to "resources/public/css/app.css"
                                                 :pretty-print? false}}]}

                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}

             :uberjar {:main app.main
                       :aot :all
                       :uberjar-name "sloth-standalone.jar"

                       :prep-tasks ["compile"
                                    ["cljsbuild" "once" "uberjar"]
                                    ["garden" "once"]]

                       :garden {:builds [{:source-paths ["src/main"]
                                          :stylesheet styles.core/app
                                          :compiler {:output-to "resources/public/css/app.css"
                                                     :pretty-print? false}}]}

                       :cljsbuild  {:builds [{:id "uberjar"
                                              :source-paths ["src/main"]
                                              :compiler {:main app.main
                                                         :output-to "resources/public/js/app.js"
                                                         :optimizations :advanced
                                                         :pretty-print false}}]}}})
