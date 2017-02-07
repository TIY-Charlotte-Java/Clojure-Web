(ns clojure-web.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])

  (:gen-class))

(defonce server (atom nil))

(defn read-purchases [category]
  (println "Type a category:")

(let [purchases (slurp "purchases.csv") ; takes in the csv
      purchases (str/split-lines purchases) ; split by line
      purchases (map (fn [line]
                       (str/split line #","))
                     purchases)
      header (first purchases) ; first line is header
      purchases (rest purchases) ; rest is stored in purchases
      purchases (map (fn [line] ; map item and category
                       (zipmap header line))
                     purchases)]
  (if (.isEmpty category)
      purchases (filter (fn [line]
                          (.equalsIgnoreCase (get line "category") category))
                        purchases))))

(defn purchases-html [category]
  (let [purchases (read-purchases category)]

    [:table
     [:a {:href "/Shoes"} "Shoes"]
     [:a {:href "/Alcohol"} "Alcohol"]
     [:a {:href "/Furniture"} "Furniture"]
     [:a {:href "Toiletries"} "Toiletries"]
     [:a {:href "Jewlry"} "Jewlry"]
     [:a {:href "Food"} "Food"]
     [:tr
      [:th (str "customer_id")]
      [:th (str "date")]
      [:th (str "credit_card")]
      [:th (str "cvv")]
      [:th (str "category")]]
     (map (fn [purchase]
            [:tr
             [:td (str (get purchase "customer_id"))]
             [:td (str (get purchase "date"))]
             [:td (str (get purchase "credit_card"))]
             [:td (str (get purchase "cvv"))]
             [:td (str (get purchase "category"))]])
          purchases)]))

(defn -main []
  (c/defroutes app
               (c/GET "/:category{.*}" [category]
                      (h/html [:html
                               [:body (purchases-html category)]])))
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))