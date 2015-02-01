(ns xrosslexior.core
  (:require [xrosslexior.letter-tree :refer :all]
            [xrosslexior.utils :refer :all]
            [clojure.math.combinatorics :refer [cartesian-product]]))

(defn string-to-sequence [word]
  (map #(keyword (clojure.string/upper-case (str %))) word))

(def black-square :█)  ; easy to type/complete at REPL

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

(defn empty-grid [m n]
  (vec (for [row (range m)]
    (vec (for [col (range n)] nil)))))

(defn empty-square-grid [n]
  (empty-grid n n))

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

(defn write-square [puzzle row col occupant]
  (assoc puzzle row (assoc (read-row puzzle row) col occupant)))

(defn rows [grid]
  (lazy-seq grid))

(defn cols [grid]
  (for [j (range (count (first grid)))]
               (read-col grid j)))

(defn spans [grid]
  (concat (rows grid) (cols grid)))

(defrecord WordspanAddress [start orientation length])

(defn offset [location displacement]
  (vec (map + location displacement)))

(defn comprising-squares [address]
  (let [step (condp = (:orientation address)
               :across [0 1]
               :down [1 0])]
    (map #(offset (:start address) (map (fn [x] (* % x)) step))
         (range (:length address)))))

(defn read-wordspan [puzzle adresss]
  (let [{:keys [start orientation length]} adresss
        [start-row start-col] start]
    (condp = orientation
      :across (subvec (read-row puzzle start-row)
                      start-col (+ start-col length))
      :down (subvec (read-col puzzle start-col)
                    start-row (+ start-row length)))))

(defn write-wordspan [puzzle address word]
  (let [{:keys [start orientation length]} address
        [start-row start-col] start]
    (reduce (fn [state [square-to-write letter-to-write]]
              (let [[row col] square-to-write]
                (write-square state row col letter-to-write)))
            puzzle
            (for [[square letter] (zip (comprising-squares address) word)]
              [square letter]))))

(defn solved? [grid]
  (every? identity
          (for [span (spans grid)]
            (some #{span} (n-dictionary (count span))))))

(defn first-blank-row-index [grid]
  (some identity
        (map-indexed (fn [index row]
                       (if (every? #(nil? %) row)
                         index
                         nil))
                     grid)))

(defn full-span? [span]
  (every? (complement nil?) span))

(defn full? [grid]
  (every? #(full-span? %) grid))

(defn already-placed [grid]
   (set (filter full-span? (spans grid))))

(defn grid-prefix-admissibles [grid]
  (let [cols (for [j (range (count (first grid)))]
               (read-col grid j))
        prefixes (map #(filter identity %) cols)
        trees (map #(letter-tree-search (n-prefix-tree (count %1)) %2)
                   cols prefixes)
        spot-admissibles (map #(keys (:children %)) trees)]
    (apply cartesian-product spot-admissibles)))

(defn grid-admissibles [grid]
  (filter #(and ((n-dictionary (count (first grid))) %)
                (not ((already-placed grid) %)))
          (grid-prefix-admissibles grid)))

(defn solve-grid [grid]
  (if (full? grid)
    grid
    (some identity
          (for [word (grid-admissibles grid)]
            (solve-grid (write-row grid (first-blank-row-index grid) word))))))

(defn display-puzzle [puzzle & condensed?]
  (let [formatter (if condensed? clojure.string/join vec)]
    (doseq [row puzzle]
      (println (formatter (map #(if ((complement nil?) %) (name %) " ")
                               row))))))
