(ns clojure_web_hw.core
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

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
                    purchases ; show all purchases if the category is a blank string
                 (filter (fn [purchase]
                           (= (get purchase "category") category))
                         purchases))]
    [:ol
     (map (fn [purchase]
            [:li (str (get purchase "customer_id") " " (get purchase "date")
                      " " (get purchase "credit_card") " " (get purchase "cvv")
                      " " (get purchase "category"))])
          purchases)]))


(c/defroutes app
             (c/GET "/:category{.*}" [category]
                    (h/html [:html
                             [:body
                              [:a {:href "/Alcohol"} "Alcohol "]
                              [:a {:href "/Furniture"} "Furniture "]
                              [:a {:href "/Toiletries"} "Toiletries "]
                              [:a {:href "/Shoes"} "Shoes "]
                              [:a {:href "/Alcohol"} "Alcohol "]
                              [:a {:href "/Jewelry"} "Jewelry "]
                              [:a {:href "/Food"} "Food "]
                              (purchases-html category)]])))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))


