(ns clojure-web.core
(:require [clojure.string :as str]
          [compojure.core :as c]
          [ring.adapter.jetty :as j]
          [hiccup.core :as h])
(:gen-class))

(defonce server (atom nil))

(defn read-purchases  [category]
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
    (if (.isEmpty category)
      purchases
      (filter (fn [line]
                (.equalsIgnoreCase (get line "category") category)) purchases))))

(defn purchases-html  [category]
  (let [purchases (read-purchases category)]
    [:table
     [:a {:href "/"} "All "]
     [:a {:href "/Furniture"} "Furniture"]
     [:a {:href "/Alcohol"} "Alcohol "]
     [:a {:href "/Toiletries"} "Toiletries "]
     [:a {:href "/Shoes"} "Shoes "]
     [:a {:href "/Food"} "Food "]
     [:a {:href "/Jewelry"} "Jewelry "]
            [:tr [:th (str "ID")]
                 [:th (str "Date")]
                 [:th (str "Credit Card")]
                 [:th (str "CVV")]
                 [:th (str "Category")]]
          (map (fn [purchase]
            [:tr [:td (str(get purchase "customer_id"))]
                 [:td (str(get purchase "date"))]
                 [:td (str(get purchase "credit_card"))]
                 [:td (str(get purchase "cvv"))]
                 [:td (str(get purchase "category"))]])
          purchases)]))

(defn -main []
  (c/defroutes app
     (c/GET "/:category{.*}" [category]
         (h/html [:html
            [:body (purchases-html category)]])))

  (when @server
    (.stop @server))
  (reset! server(j/run-jetty app {:port 3000 :join? false}))
  )