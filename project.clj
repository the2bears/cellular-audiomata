(defproject cellular-audiomata "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [clojure-lanterna "0.9.4"]
                 [overtone "0.10.1"]
                 [org.clojure/test.check "0.9.0"]]
  :main ^:skip-aot cellular-audiomata.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
