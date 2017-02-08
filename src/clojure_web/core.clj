(ns clojure-web.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])

  (:gen-class))


(defn purchases [category-name]
  (println "Pick a Cateogry:")
  (println "Alcohol")
  (println "Furniture")
  (println "Shoes")
  (println "Jewelry")
  (println "Toiletries")
  (println "Food")

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
    (if (.isEmpty category-name)
      purchases
      (filter (fn [line]
                (.equalsIgnoreCase (get line "category") category-name)) purchases))))


(defn purchases-html [category-name]
  (let [purchases (purchases category-name)]

    [:table
     [:ul]
     [:li [:a {:href "/Alcohol"} "Alcohol"]]
     [:li [:a {:href "/Furniture"} "Furniture"]]
     [:li [:a {:href "/Toiletries"} "Toiletries"]]
     [:li [:a {:href "/Jewelry"} "Jewelry"]]
     [:li [:a {:href "/Food"} "Food"]]
     [:tr [:th  (str "customer_id") ]
      [:th (str "date")]
      [:th (str "credit_card")]
      [:th (str "cvv")]
      [:th (str "category")]]
     (map (fn [purchase]
            [:tr [:td (str (get purchase "customer_id") )]
             [:td (str (get purchase "date"))]
             [:td (str (get purchase "credit_card"))]
             [:td (str (get purchase "cvv"))]
             [:td (str (get purchase "category"))] ])
          purchases)]))

(defonce server (atom nil))



(defn -main []

  (c/defroutes app
               (c/GET "/:category{.*}" [category] ; changed country (from class work) to category
                      (h/html [:html
                               [:body (purchases-html category)]])))
  (when @server
    (.stop @server))

  (reset! server (j/run-jetty app {:port 3000 :join? false})))
