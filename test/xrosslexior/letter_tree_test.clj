(ns xrosslexior.letter-tree-test
  (:require [clojure.test :refer :all]
            [xrosslexior.core :refer :all]
            [xrosslexior.letter-tree :refer :all]))

(def restricted-dictionary (map string-to-sequence
                                ["cat" "car" "cdr" "comparison"
                                 "rah"
                                 "sick" "sic"]))

(deftest test-partition-by-initial
  (is (= (partition-by-initial (map string-to-sequence
                                    ["rah" "cat" "comparison"]))
         {:R [[:R :A :H]]
          :C [[:C :A :T] [:C :O :M :P :A :R :I :S :O :N]]})))

(deftest test-letter-tree-builder
  (let [letter-tree (letter-tree-builder nil restricted-dictionary)]
    (is (= (set (map #(:letter %) (:children letter-tree)))
           #{:C :R :S}))))

(deftest test-letter-tree-search
  (let [letter-tree (build-letter-tree restricted-dictionary)]
    (is (letter-tree-search letter-tree []))             ; empty
    (is (letter-tree-search letter-tree [:C :A :R]))     ; entire word
    (is (letter-tree-search letter-tree [:C :O :M :P]))  ; partial word
    (is (not (letter-tree-search letter-tree [:X])))     ; fail fast
    (is (not (letter-tree-search letter-tree [:C :O :M :P :A :X]))))) ; fail
