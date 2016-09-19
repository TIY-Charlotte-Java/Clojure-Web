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


(defn purchases-html [category]
  (let [purchases (read-purchases)
        purchases (if (= 0 (count category))
                 purchases
                 (filter (fn [purchases]
                           (= (get purchases "category") category))
                         purchases))]

    [:div
     [:a {:href "/Alcohol"} "Alcohol "]
     [:a {:href "/Furniture"} "Furniture "]
     [:a {:href "/Toiletries"} "Toiletries "]
     [:a {:href "/Shoes"} "Shoes "]
     [:a {:href "/Jewelry"} "Jewelry "]
     [:a {:href "/"} "All"]
     [:ol

      (map (fn [purchases]
             [:li (str (get purchases "customer_id") " " (get purchases "date") " " (get purchases "credit_card") " "
                       (get purchases "cvv") " " (get purchases "category"))])
           purchases)]]
    ))

(c/defroutes app
             (c/GET "/:category{.*}" [category]
                    (h/html [:html
                             [:body
                              (purchases-html category)]])))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))
