(defproject xrosslexior "0.0-dev"
  :description "generate crossword puzzles, maybe"
  :url "http://github.com/zackmdavis/xrosslexior"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :main xrosslexior.core
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/math.combinatorics "0.0.8"]]
  :plugins [[com.jakemccrary/lein-test-refresh "0.5.0"]])
