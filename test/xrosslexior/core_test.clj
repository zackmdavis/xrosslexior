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

(deftest test-valid-prefix-detection
  (is (valid-prefix? (n-dictionary 3) [:C :A]))
  (is (valid-prefix? (n-dictionary 3) [:C :A :T]))
  (is (valid-prefix? (n-dictionary 4) [:R :A :T])))

(deftest test-solution-detection
  (is (time (solved? [[:B :A :T :E]
                      [:A :B :E :L]
                      [:N :E :A :L]]))))

(deftest test-fullness-detection
  (is (not (full? [[nil nil] [nil nil]])))
  (is (not (full? [[:A :B] [:C nil]])))
  (is (full? [[:A :B] [:C :D]])))
