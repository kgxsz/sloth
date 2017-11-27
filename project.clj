(defproject sloth "0.0.1"
  :description "sloth"
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [org.clojure/clojurescript "1.9.908"]
                 [org.omcljs/om "1.0.0-beta1"]
                 [fulcrologic/fulcro "1.0.0-beta10"]
                 [com.stuartsierra/component "0.3.2"]
                 [garden "1.3.3"]
                 [com.powernoodle/normalize "7.0.0"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]]

  :source-paths ["src/main"]
  :resource-paths ["resources"]
  :clean-targets ^{:protect false} ["resources/public/js"
                                    "resources/public/css"
                                    "target"
                                    ".nrepl-port"]

  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-garden "0.3.0"]]

  :aliases {"build" ["do"
                     "clean"
                     ["cljsbuild" "once" "min"]]}

  :prep-tasks [["garden" "once"]]

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/main" "src/dev"]

                :figwheel     {:on-jsload "cljs.user/refresh"}
                :compiler     {:main                 cljs.user
                               :output-to            "resources/public/js/app.js"
                               :output-dir           "resources/public/js/app"
                               :preloads             [devtools.preload]
                               :asset-path           "js/app"
                               :optimizations        :none}}
               {:id           "min"
                :source-paths ["src/main"]
                :compiler     {:main            app.main
                               :output-to       "resources/public/js/app.js"
                               :optimizations   :advanced
                               :pretty-print    false}}]}

  :figwheel {:css-dirs ["resources/public/css"]
             :server-logfile "target/figwheel_temp/logs/figwheel_server.log"}

  :garden {:builds [{:source-paths ["src/main"]
                     :stylesheet styles.core/app
                     :compiler {:output-to "resources/public/css/app.css"
                                :pretty-print? false}}]}

  :profiles {:dev {:source-paths ["src/dev" "src/main"]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [figwheel-sidecar "0.5.13"]
                                  [org.clojure/tools.nrepl "0.2.13"]]}})
