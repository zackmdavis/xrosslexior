(ns xrosslexior.letter-tree)

(defrecord LetterTreeNode [letter children])

(defmethod print-method LetterTreeNode [ltn writer]
  (print-method 'LetterTreeNode| writer)
  (print-method (:letter ltn) writer)
  (print-method '->  writer)
  (print-method (map #(:letter %) (:children ltn)) writer))

(def letters (map (comp keyword str char) (concat (range 65 91))))

(defn partition-by-initial [words]
  (into {}
        (filter #(seq (second %))
                (for [letter letters]
                  [letter (filter #(= letter (first %)) words)]))))

(defn letter-tree-builder [letter postfixes]
  (->LetterTreeNode letter
                    (set  ; XXX: consider making `children` a map instead
                     (for [[initial postset] (partition-by-initial postfixes)]
                       (letter-tree-builder initial (map rest postset))))))

(defn build-letter-tree [words]
  (letter-tree-builder nil words))

(defn letter-tree-search [tree query]
  (if (empty? query)
    true
    (let [sought (first query)
          initials (map #(:letter %) (:children tree))]
      (if (some #{sought} initials)
        (letter-tree-search (first (filter #(= sought (:letter %))
                                           (:children tree)))
                            (rest query))
        false))))
