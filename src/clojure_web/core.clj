(ns clojure-web.core
  (:gen-class)
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h]))

(defn read-purchases []
  (let [purchases (slurp "purchases.csv")
        purchases (str/split-lines purchases)
        purchases (map (fn [line]
                      (str/split line #","))
                    purchases)
        header (first purchases)
        purchases (rest purchases)
        purchases (map (fn [line]
                      (zipmap header line))
                    purchases)]
    purchases))

(defn purchases-html []
  (let [purchases (read-purchases)]
    [:ol
     (map (fn [purchases]
            [:li (str (get purchases "customer_id") " " (get purchases "category"))])
          purchases)]))

(c/defroutes app
             (c/GET "/:category{.*}" [category]
                    (h/html [:html
                             [:body
                              (purchases-html)]])))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))