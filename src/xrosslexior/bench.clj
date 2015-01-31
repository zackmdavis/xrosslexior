(ns xrosslexior.bench
  (:require [xrosslexior.core :refer :all]))

(defmacro benchmark [code]
  `(let [start# (. System (nanoTime))
         evaluated# ~code
         stop# (. System (nanoTime))]
     {:result evaluated#
      :time (/ (- stop# start#) 1e9)}))

(defn benchmark-first-row-words [solver words]
  (let [grids (map #(write-row (empty-square-grid (count %)) 0 %) words)]
    (map #(benchmark (solver %)) grids)))
