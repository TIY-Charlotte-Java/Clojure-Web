(ns clojure-web.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))

(defonce server (atom nil))

(defn purchaseList []
  (println "What category would you like to filter by...")
  (let
    [purchases (slurp "purchases.csv")
     purchases (str/split-lines purchases)
     purchases (map (fn [line]
                      (str/split line #","))
                    purchases)
     header (first purchases)
     purchases (rest purchases)
     purchases (map (fn [line]
                      (zipmap header line))
                    purchases)
     ;category (read-line)
     ;purchases (filter (fn [line]
     ;                    (= (get line "category") category))
     ;                  purchases)
     ]
     purchases))

(defn purchases-html [category]
  (let [purchases (purchaseList)
        purchases (if (= 0 (count category))
              purchases
              (filter (fn [purchases]
                      (= (get purchases "category") category))
                              purchases))]
     [:ol
      (map (fn [purchases]
             [:li (str (get purchases "customer_id") " "
                       (get purchases "date") " "
                       (get purchases "credit_card") " "
                       (get purchases "cvv") " "
                       (get purchases "category"))])
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
  (reset! server (j/run-jetty app {:port 3001 :join? false})))
