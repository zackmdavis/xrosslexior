(ns xrosslexior.core
  (:require [xrosslexior.letter-tree :refer :all]
            [xrosslexior.patterns :refer :all]
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

(def other
  {:across :down
   :down :across})

(def orientation-to-span-type
  {:across :row
   :down :col})

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

(defn read-span [puzzle index orientation]
  (condp = orientation
    :across (read-row puzzle index)
    :down (read-col puzzle index)))

(defn write-square [puzzle row col occupant]
  (assoc puzzle row (assoc (read-row puzzle row) col occupant)))

(defn rows [grid]
  (lazy-seq grid))

(defn cols [grid]
  (for [j (range (count (first grid)))]
               (read-col grid j)))

(defn spans
  ([grid] (concat (rows grid) (cols grid)))
  ([grid orientation] (condp = orientation
                        :across (rows grid)
                        :down (cols grid))))

(defn map->location [{:keys [row col]}]
  [row col])

(defn location-as-map [[row col]]
  {:row row :col col})

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

(defn clear-oriented-length [puzzle location orientation]
  (let [[span-index counter-index] (condp = orientation
                                     :across location
                                     :down (reverse location))
        span-selector (condp = orientation
                        :across read-row
                        :down read-col)
        clear-span (take-while #(not= :█ %)
                               (drop counter-index
                                     (span-selector puzzle span-index)))]
    (count clear-span)))


(defn wordspan-addresses-oriented [puzzle orientation]
  (apply
   concat
   (for [[span-index indexed-span] (enumerate (map #(enumerate %)
                                                   (spans puzzle orientation)))
         :let [partitioned (partition-by (fn [[counter-index value]]
                                           (not= :█ value))
                                         indexed-span)]]
     (filter
      identity
      (map (fn [subspan]
             (let [[opening-counter-index opening-value] (first subspan)
                   [row-index col-index] (condp = orientation
                                           :across [span-index
                                                    opening-counter-index]
                                           :down [opening-counter-index
                                                  span-index])]
               (if (not= opening-value :█)
                 (->WordspanAddress [row-index col-index]
                                    orientation
                                    (count subspan))
                 nil)))
           partitioned)))))

(defn wordspan-addresses-across [puzzle]
  (wordspan-addresses-oriented puzzle :across))

(defn wordspan-addresses-down [puzzle]
  (wordspan-addresses-oriented puzzle :down))

(defn containing-address-oriented [puzzle location orientation]
  (let [[span-index counter-index] (condp = orientation
                                     :across location
                                     :down (reverse location))
        containing-span (read-span puzzle span-index orientation)
        candidate-addresses (filter
                             (fn [address]
                               (= span-index
                                  ((location-as-map (:start address))
                                   (orientation-to-span-type orientation))))
                             (wordspan-addresses-oriented puzzle orientation))]
    (some (fn [address]
            (let [counter-start ((location-as-map (:start address))
                                 (orientation-to-span-type
                                  (other orientation)))]
              (if (<= counter-start counter-index (+ counter-start
                                                     (:length address)))
                address
                nil)))
          candidate-addresses)))

(defn containing-address-down [puzzle location]
  (containing-address-oriented puzzle location :down))

(defn containing-address-across [puzzle location]
  (containing-address-oriented puzzle location :across))

(defn blank-address? [puzzle addresss]
  (let [wordspan (read-wordspan puzzle addresss)]
    (every? nil? wordspan)))

(defn first-blank-across-address [puzzle]
  (first (filter #(blank-address? puzzle %)
          (wordspan-addresses-across puzzle))))

(defn down-addresses-athwart-across [puzzle across-address]
  (let [squares-traversed (comprising-squares across-address)]
    (map #(containing-address-down puzzle %) squares-traversed)))

(defn down-prefix-admissibles [puzzle across-address & {:keys [used]
                                                        :or {used #{}}}]
  (let [down-addresses (down-addresses-athwart-across puzzle across-address)
        down-wordspans (map #(read-wordspan puzzle %) down-addresses)
        prefixes (map #(filter identity %) down-wordspans)
        trees (map (fn [address prefix]
                     (letter-tree-search (n-prefix-tree (:length address))
                                         prefix))
                   down-addresses prefixes)
        spot-admissibles (map #(keys (:children %)) trees)
        ;; a fairly ad hoc technique to avoid placing duplicate words
        ;; in the orthogonal direction
        spot-admissible-uniquelies ;\
        (map-indexed
         (fn [index admissibles]
           (filter (fn [admissible]
                     (not (used (conj (vec (nth prefixes index)) admissible))))
                   admissibles))
         spot-admissibles)]
    (apply cartesian-product spot-admissible-uniquelies)))

(defn admissibles [puzzle across-address & {:keys [used diagnostic]
                                            :or {used #{} diagnostic false}}]
  (let [words-fitting-across (n-dictionary (:length across-address))
        strings-admissible-down (down-prefix-admissibles puzzle across-address
                                                       :used used)]
    (when diagnostic
      (println {:words-fitting-across (count words-fitting-across)
                :strings-admissible-down (count strings-admissible-down)}))
  (filter (set (filter #(not (used %)) words-fitting-across))
          strings-admissible-down)))

(defn solve-puzzle [puzzle & {:keys [used spy diagnose-admissibles]
                              :or {used #{} spy false
                                   diagnose-admissibles false}}]
  (when (or (and (= (type spy) java.lang.Double) (< (rand) spy))
            (= spy true))
    (println "spying ...")
    (display-puzzle puzzle))
  (if (full? puzzle)
    puzzle
    (let [next-across-address (first-blank-across-address puzzle)]
      (some identity
            (let [admissible-words
                  (admissibles puzzle next-across-address
                               :used used
                               :diagnostic diagnose-admissibles)]
              (for [word admissible-words]
                (solve-puzzle (write-wordspan puzzle next-across-address word)
                              :spy spy :used (conj used word)
                              :diagnose-admissibles diagnose-admissibles)))))))
