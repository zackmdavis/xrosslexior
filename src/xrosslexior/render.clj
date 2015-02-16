(ns xrosslexior.render
  (:require [clojure.java.io :refer :all]
            [clojure.string :refer [join]]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5]]
            [xrosslexior.core :refer :all]
            [xrosslexior.utils :refer :all]
            [xrosslexior.patterns :refer :all]))

(defn represent-square [i j value]
  (let [essential-class "square"
        indices {:data-row i :data-col j}
        secondary-class (cond (= (type value) java.lang.Long) "clue-starter"
                              (= value :█) "barrier")
        content (if (= (type value) java.lang.Long)
                  [:div {:class "clue-index"} value])]
    [:div (merge {:class (join " " [essential-class secondary-class])}
                 indices)
     content]))

(defn represent-grid [grid]
  (for [[i row] (enumerate (impose-numbering grid))]
    [:div {:class "row"}
     (for [[j square] (enumerate row)]
       (represent-square i j square))]))

(def encoding-disclaimer
  [:meta {:content "text/html;charset=utf-8"
          :http-equiv "Content-Type"}])

(defn write-page [grid]
  (let [base-directory (System/getProperty "user.dir")
        destination (file base-directory "resources" "puzzle.html")]
    (with-open [that-which-writes (writer destination)]
      (.write that-which-writes
              (html5 [:head encoding-disclaimer
                      [:link {:type "text/css", :href "puzzle.css",
                              :rel "stylesheet"}]]
                     (represent-grid grid))))))
