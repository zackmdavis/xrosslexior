(ns xrosslexior.core
  (:require [xrosslexior.letter-tree :refer :all]
            [clojure.math.combinatorics :refer [cartesian-product]]))

(defn string-to-sequence [word]
  (map #(keyword (clojure.string/upper-case (str %))) word))

(def our-dictionary
  (map string-to-sequence
       (filter (fn [word] (not (.contains word "'")))
               (clojure.string/split-lines
                (slurp "/usr/share/dict/words")))))

(defn compile-n-dictionary [n]
  (set (filter #(= (count %) n) our-dictionary)))
(def n-dictionary (memoize compile-n-dictionary))

(defn compile-n-prefix-tree [n]
  (build-letter-tree (n-dictionary n)))
(def n-prefix-tree (memoize compile-n-prefix-tree))

(defn prefix? [word letters]
  (= letters (subvec (vec word) 0 (count letters))))

(defn valid-prefix? [dictionary letters]
  ;; XXX: really contrived means
  (let [true-letters (filter identity letters)]
    (letter-tree-search
     ;; ... to keep the same argument signature
     (n-prefix-tree (count (first dictionary)))
     true-letters)))

(defn empty-grid [m n]
  (vec (for [row (range m)]
    (vec (for [col (range n)] nil)))))

(defn lookup [grid coordinates]
  ((grid (first coordinates)) (second coordinates)))

(defn write [grid coordinates letter]
  (assoc grid
         (first coordinates)
         (assoc (grid (first coordinates)) (second coordinates) letter)))

(defn read-row [grid row]
  (grid row))

(defn write-row [grid row word]
  (assoc grid row (vec word)))

(defn read-col [grid col]
  (vec (map #(nth % col) grid)))

(defn write-col [grid col word]
  (reduce (fn [state row] (write state [row col] (nth word row)))
          grid
          (range (count word))))

(defn solved? [grid]
  (let [width (count (read-row grid 0))
        height (count (read-col grid 0))]
    (every? identity
            (concat (for [i (range height)]
                      (some #{(read-row grid i)} (n-dictionary width)))
                    (for [j (range width)]
                      (some #{(read-col grid j)} (n-dictionary height)))))))

(defn solvable? [grid]
  (let [width (count (read-row grid 0))
        height (count (read-col grid 0))]
    (every? identity
            (concat (for [i (range height)]
                      (valid-prefix? (n-dictionary width)
                                     (read-row grid i)))
                    (for [j (range width)]
                      (valid-prefix? (n-dictionary height)
                                     (read-col grid j)))))))

(defn first-blank-row-index [grid]
  (some identity
        (map-indexed (fn [index row]
                       (if (every? #(nil? %) row)
                         index
                         nil))
                     grid)))

(defn solve [grid]
  (if (solved? grid)
    grid
    (when (solvable? grid)
      (let [next-row-index (first-blank-row-index grid)]
        (some identity
              (for [word (n-dictionary (count (read-row grid next-row-index)))]
                (solve (write-row grid next-row-index word))))))))


(defn full? [grid]
  (every? #(every? (comp not nil?) %) grid))

(defn prefix-admissibles [grid]
  ;; we're assuming square grids for now
  (let [prefixes (map #(filter identity %) (for [j (range (count grid))]
                                             (read-col grid j)))
        trees (map #(letter-tree-search (n-prefix-tree (count grid)) %)
                   prefixes)
        spot-admissibles (map #(keys (:children %)) trees)]
    (apply cartesian-product spot-admissibles)))

(defn admissibles [grid]
  (filter (n-dictionary (count grid)) (prefix-admissibles grid)))

(defn solve-it [grid]
  (if (full? grid)
    grid
    (some identity
          (for [word (admissibles grid)]
            (solve (write-row grid (first-blank-row-index grid) word))))))
