(ns xrosslexior.core-test
  (:require [clojure.test :refer :all]
            [xrosslexior.core :refer :all]))

(deftest test-write-lookup
  (is (= :A
         (lookup (write (empty-grid 2 2) [0 0] :A)
                 [0 0]))))

(deftest test-prefix-detection
  (doseq [prefix [[] [:S :C] [:S :C :O :N :E]]]
    (is (prefix? [:S :C :O :N :E] prefix)))
  (is (not (prefix? [:S :C :O :N :E] [:C :O]))))

(def my-sample-puzzle
  [[:A :B :C :█]
   [:D :E :F :█]
   [:█ :G :H :█]
   [:I :J :K :L]
   [:█ :█ :M :N]])

(deftest test-spans
  (is (= (count (spans my-sample-puzzle)) 9))
  (is (= (count (spans my-sample-puzzle :across)) 5))
  (is (= (first (spans my-sample-puzzle :down))
         [:A :D :█ :I :█])))

(deftest test-comprising-squares
  (is (= (comprising-squares (->WordspanAddress [0 0] :across 3))
         [[0 0] [0 1] [0 2]]))
  (is (= (comprising-squares (->WordspanAddress [0 0] :down 3))
         [[0 0] [1 0] [2 0]])))

(deftest test-wordspan-readability
  (is (= (read-wordspan my-sample-puzzle (->WordspanAddress [0 0] :across 3))
         [:A :B :C]))
  (is (= (read-wordspan my-sample-puzzle (->WordspanAddress [0 1] :down 4))
         [:B :E :G :J])))

(deftest test-square-writability
  (is (= (write-square (write-square my-sample-puzzle 0 0 :X) 1 1 :Y)
         [[:X :B :C :█]
          [:D :Y :F :█]
          [:█ :G :H :█]
          [:I :J :K :L]
          [:█ :█ :M :N]])))

(deftest test-wordspan-writability
  (is (= (write-wordspan my-sample-puzzle
                         (->WordspanAddress [0 0] :across 3)
                         [:X :Y :Z])
         [[:X :Y :Z :█]
          [:D :E :F :█]
          [:█ :G :H :█]
          [:I :J :K :L]
          [:█ :█ :M :N]])))

(deftest test-wordspan-addresses-across
  (is (= (wordspan-addresses-across my-sample-puzzle)
         (map (fn [[location length]]
                (->WordspanAddress location :across length))
              [[[0 0] 3] [[1 0] 3] [[2 1] 2] [[3 0] 4] [[4 2] 2]]))))

(deftest test-wordspan-addresses-down
  (is (= (wordspan-addresses-down my-sample-puzzle)
         (map (fn [[location length]]
                (->WordspanAddress location :down length))
              [[[0 0] 2] [[3 0] 1] [[0 1] 4] [[0 2] 5] [[3 3] 2]]))))

(deftest test-impose-numbering
  (is (= (impose-numbering my-sample-puzzle)
         [[ 1  2  3 :█]
          [ 4 :E :F :█]
          [:█  5 :H :█]
          [ 6 :J :K  7]
          [:█ :█  8 :N]])))

(deftest test-containing-address-down
  (is (= (containing-address-down my-sample-puzzle [3 1])
         (->WordspanAddress [0 1] :down 4)))
  (is (= (containing-address-down my-sample-puzzle [4 3])
         (->WordspanAddress [3 3] :down 2)))
  (is (= (containing-address-down my-sample-puzzle [3 0])
         (->WordspanAddress [3 0] :down 1))))

(deftest test-first-blank-across-address
  (let [our-puzzle (empty-square-grid 3)]
    (is (= (first-blank-across-address our-puzzle)
           (->WordspanAddress [0 0] :across 3)))
    (is (= (first-blank-across-address
            (write-wordspan our-puzzle
                            (first-blank-across-address our-puzzle)
                            [:B :I :T]))
           (->WordspanAddress [1 0] :across 3)))))

(deftest test-blank-address-detection
  (let [our-puzzle [[nil :A] [nil nil]]]
    (is (not (blank-address? our-puzzle (->WordspanAddress [0 0] :across 2))))
    (is (blank-address? our-puzzle (->WordspanAddress [1 0] :across 2)))))

(deftest test-down-addresses-athwart-across
  (is (= (down-addresses-athwart-across my-sample-puzzle
                                        (->WordspanAddress [3 0] :across 4))
         (map #(apply ->WordspanAddress %)
              [[[3 0] :down 1] [[0 1] :down 4]
               [[0 2] :down 5] [[3 3] :down 2]]))))

(deftest test-solution-detection
  (is (solved? [[:L :I :S :P]
                [:A :L :O :E]
                [:S :K :Y :E]
                [:T :S :A :R]]))
  (is (solved? [[:G :H :O :S :T]
                [:L :A :M :A :R]
                [:A :L :A :N :A]
                [:D :O :R :K :Y]])))

(deftest test-already-placed
  (is (= (already-placed [[:C :A :T]
                          [:O nil nil]
                          [:D nil nil]])
         #{[:C :A :T] [:C :O :D]})))

(deftest test-fullness-detection
  (is (not (full? [[nil nil] [nil nil]])))
  (is (not (full? [[:A :B] [:C nil]])))
  (is (full? [[:A :B] [:C :D]])))
