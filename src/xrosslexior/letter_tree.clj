(ns xrosslexior.letter-tree)

(defrecord LetterTreeNode [letter children])

(defmethod print-method LetterTreeNode [ltn writer]
  (print-method 'LetterTreeNode| writer)
  (print-method (:letter ltn) writer)
  (print-method '->  writer)
  (print-method (map #(:letter %) (vals (:children ltn))) writer))

(def alphabet (map (comp keyword str char) (concat (range 65 91))))

(defn partition-by-initial [words]
  (into {}
        (filter #(seq (second %))
                (for [letter alphabet]
                  [letter (filter #(= letter (first %)) words)]))))

(defn letter-tree-builder [letter postfixes]
  (->LetterTreeNode
   letter
   (into {}
         (for [[i postfix-group] (partition-by-initial postfixes)]
           [i (letter-tree-builder i (map rest postfix-group))]))))

(defn build-letter-tree [words]
  (letter-tree-builder nil words))

(defn letter-tree-search [tree query]
  (if (empty? query)
    tree
    (let [seeking (first query)
          retrieved ((:children tree) seeking)]
      (if retrieved
        (letter-tree-search retrieved (rest query))
        false))))
