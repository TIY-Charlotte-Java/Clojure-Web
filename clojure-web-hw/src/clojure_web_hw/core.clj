(ns clojure_web_hw.core
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [compojure.core :as c] ;used to define get route
            [ring.adapter.jetty :as j] ;used to start web server
            [hiccup.core :as h])
  (:gen-class))

(defn read-purchases [] ;instead of main, make own function to read the csv
  (let [purchases (slurp "purchases.csv") ;read csv
        purchases (str/split-lines purchases) ;split on different lines
        purchases (map (fn [line]
                      (str/split line #",")) ;by comma
                       purchases)
        header (first purchases) ;variable header that takes the first line of purchases
        purchases (rest purchases) ;and it contains all the rest
        purchases (map (fn [line]
                      (zipmap header line));combine line and header maps
                       purchases)]
    purchases))

(defn purchases-html [category] ;function that creates html.  takes category string as path variable
  (let [purchases (read-purchases) ;calls read-purchases
        purchases (if (= 0 (count category)) ;
                    purchases ; show all purchases if the category is a blank string
                 (filter (fn [purchase] ;filter function called purchase
                           (= (get purchase "category") category)) ;uses path variable for filter
                         purchases))]
    [:ol ;ordered list for purchases, showing all columns
     (map (fn [purchase]
            [:li (str (get purchase "customer_id") " " (get purchase "date")
                      " " (get purchase "credit_card") " " (get purchase "cvv")
                      " " (get purchase "category"))])
          purchases)]))


(c/defroutes app
             (c/GET "/:category{.*}" [category] ;accepts category as path variable.  allows path variable to be blank string
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

(defonce server (atom nil)) ;to let us run main as many times as we want. resets contents

(defn -main [] ;now we'll use main function to start the server
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false}))) ;join false lets server run in background


