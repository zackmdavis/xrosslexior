(ns xrosslexior.bench-test
  (:require [clojure.test :refer :all]
            [xrosslexior.bench :refer :all]))

(deftest development-bench
  ;; No-assertions "test" to be run automatically by test-refresh in
  ;; the hopes of giving some rough feedback about whether hacking is
  ;; helping or hurting performance
  (println (time (convenient-experiment 5 25))))
