(ns xrosslexior.core
  (:require [xrosslexior.letter-tree :refer :all]))

(defn string-to-sequence [word]
  (map #(keyword (clojure.string/upper-case (str %))) word))

(def our-dictionary
  (map string-to-sequence
       (filter (fn [word] (not (.contains word "'")))
               (clojure.string/split-lines
                (slurp "/usr/share/dict/words")))))

(defn compile-n-dictionary [n]
  (filter #(= (count %) n) our-dictionary))
(def n-dictionary (memoize compile-n-dictionary))

(defn prefix? [word letters]
  (= letters (subvec (vec word) 0 (count letters))))

(defn valid-prefix? [dictionary letters]
  (let [prefix (take-while #(not (nil? %)) letters)]
    (some #(prefix? % prefix) dictionary)))

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
