(defproject clojure-web-hw "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :main clojure_web_hw.core                                  ;need to define main method
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"] [org.clojure/data.json "0.2.6"]
                 [ring "1.4.0"]
                 [hiccup "1.0.5"]
                 [compojure "1.5.0"]]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [clojure_web_hw.core])


