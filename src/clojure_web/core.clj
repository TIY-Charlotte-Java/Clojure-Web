(ns clojure-web.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))


(defn read-puchases [category-name]
  (let [purchases (slurp "purchases.csv") ;;stores the purchases.csv file into purchases
        purchases (str/split-lines purchases) ;;splits by each line
        purchases (map (fn [line]
                         (str/split line #",")) ;;removes the commas
                       purchases)
        header (first purchases) ;;stores the first line into header
        purchases (rest purchases) ;;stores the rest into purchases
        purchases (map (fn [line]  ;;makes a hashmap using header as the categories on which to populate
                         (zipmap header line))
                       purchases)]
    (if (.isEmpty category-name) ;;making sure the category-name exists
      purchases
      (filter (fn [line]
                (.equalsIgnoreCase (get line "category") category-name)) purchases))))

;; converts the file to json

(defn purchases-html [category-name] ;; function creation with purpose of conversion to html
  (let [purchases (read-puchases category-name)] ;; each entry is a map of person
    ;; Table creation
    [:table ; ordered list in Html
     [:a {:href "/Alcohol"} "Alcohol"]:&nbsp&nbsp
     [:a {:href "/Furniture"} "Furniture"]:&nbsp&nbsp
     [:a {:href "/Toiletries"} "Toiletries"]:&nbsp&nbsp
     [:a {:href "/Jewelry"} "Jewelry"]:&nbsp&nbsp
     [:a {:href "/Food"} "Food"]:&nbsp&nbsp
     [:tr [:th  (str "customer_id")]
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

(defonce server (atom nil)) ;;modify an object through atom

(defn -main []
  (c/defroutes app ;; defines route forms - calling it app
               (c/GET "/:category{.*}" [category] ;; route is specified  - get reqeust to "/ ; url mapping * 0 or many
                      (h/html [:html
                               [:body (purchases-html category)]]))) ;; what is returned
  (when @server
    (.stop @server))

  (reset! server (j/run-jetty app {:port 3000 :join? false}))) ;; local server

