(ns xrosslexior.utils)

(defmacro map-comprehension [bindings map-entry]
  `(into {} (for ~bindings ~map-entry)))

(defn zip [& seqs]
  (apply map vector seqs))

(defn into-sorted-map-by-fn-of-values [transform regular-map]
  (into
   (sorted-map-by
    (fn [first-key another-key]
      (compare [(transform (regular-map first-key)) first-key]
               [(transform (regular-map another-key)) another-key])))
   regular-map))

(defn into-sorted-map-by-values [regular-map]
  (into-sorted-map-by-fn-of-values identity regular-map))
