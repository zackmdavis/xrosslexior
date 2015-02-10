(ns xrosslexior.render
  (:require [xrosslexior.core :refer :all]
            [xrosslexior.utils :refer :all]
            [hiccup.core :refer [html]]))

(defn render-grid [grid]
  (html (for [[i row] (enumerate grid)]
          [:div (for [[j square] (enumerate row)]
                  [:div {:row i :col j} square])])))
