(ns xrosslexior.bench
  (:require [xrosslexior.core :refer :all])
  (:require [xrosslexior.utils :refer :all]))

(defmacro benchmark [code]
  `(let [start# (. System (nanoTime))
         evaluated# ~code
         stop# (. System (nanoTime))]
     {:output evaluated#
      :time (/ (- stop# start#) 1e9)}))

(defn square-grid-seeded-with-word [word]
  (write-row (empty-square-grid (count word)) 0 word))

(defn benchmark-first-row-words [solver words]
  (let [grids (map square-grid-seeded-with-word words)]
    (map #(benchmark (solver %)) grids)))

(defn mean [data]
  (/ (reduce + data) (count data)))

(defn sample-variance [data]
  (- (/ (reduce + (map #(Math/pow % 2) data))
        (count data))
     (Math/pow (mean data) 2)))

(defn sample-standard-deviation [data]
  (Math/sqrt (sample-variance data)))

(defn summarize-benchmark [results]
  (let [times (map #(% :time) results)]
      {:avrg (mean times)
       :stdv (sample-standard-deviation times)}))

(defn convenient-experiment [n size]
  (let [start (rand-int 300)
        words (take size (drop start (n-dictionary n)))
        results (map-comprehension [[name solver] [[:std solve-grid]]]
                  [name (benchmark-first-row-words solver words)])
        summary (map-comprehension [[solver-name result] results]
                  [solver-name (summarize-benchmark result)])]
    summary))
