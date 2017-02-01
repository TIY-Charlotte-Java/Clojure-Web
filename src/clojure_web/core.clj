(ns clojure-filtering.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])

  (:gen-class))


(defn read-puchases [category-name]
  (println "Pick a Cateogry:")
  (println "Alcohol")
  (println "Furniture")
  (println "Shoes")
  (println "Jewelry")
  (println "Toiletries")
  (println "Food")

  (let [purchases (slurp "purchases.csv") ;take it all in an store it in purchases
        purchases (str/split-lines purchases) ; makes it into each line containing vector
        purchases (map (fn [line]
                         (str/split line #",")) ; splits at the vector and produces a line without ,
                       purchases)
        header (first purchases) ;separates the header from the rest of the line
        purchases (rest purchases) ; redefines the purchases
        purchases (map (fn [line]  ; makes a hashmap with category and item
                         (zipmap header line))
                       purchases)]
    (if (.isEmpty category-name)
      purchases
      (filter (fn [line]
                (.equalsIgnoreCase (get line "category") category-name)) purchases))))

    ; writes the file to json

    (defn purchases-html [category-name] ; take the people and map it to its html
      (let [purchases (read-puchases category-name)] ; each entry is a map of person
        [:table ; ordered list in Html
         (map (fn [purchase]
                [:tr [:td :customer_id]]
                [:tr [:td (str (get purchase "customer_id") )]
                     [:td (str (get purchase "date"))]
                     [:td (str (get purchase "credit_card"))]
                     [:td (str (get purchase "cvv"))]
                     [:td (str (get purchase "category"))] ]); map for every line in people call that line person, replace that with - vector
              purchases)]))

    (defonce server (atom nil))



    (defn -main [] ;modify an object through atom
      ; operaand is a server and the second mehtod

      (c/defroutes app ; defines route forms - calling it app
                   (c/GET "/:category{.*}" [category] ; route is specified  - get reqeust to "/ ; url mapping * 0 or many
                          (h/html [:html ; 0 or many countiries and pass in that
                                    [:body (purchases-html category)]]))) ; body that is returned
      (when @server
        (.stop @server))

      (reset! server (j/run-jetty app {:port 3000 :join? false}))) ; boot server and the app from main is going to be passed

