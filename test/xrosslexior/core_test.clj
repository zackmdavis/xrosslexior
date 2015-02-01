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
