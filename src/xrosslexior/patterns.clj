(ns xrosslexior.patterns)


(def seven-eleven-pattern  [[nil nil nil nil nil :█  nil nil nil nil nil]
                            [nil nil nil nil nil :█  nil nil nil nil nil]
                            [nil nil nil nil :█  nil nil nil nil nil nil]
                            [nil nil nil :█  nil nil nil :█  nil nil nil]
                            [:█  nil nil nil nil nil :█  nil nil nil :█ ]
                            [:█  nil nil nil nil :█  nil nil nil nil :█ ]
                            [:█  :█  nil nil nil :█  nil nil nil :█  :█ ]])

(def thirteen-eleven-pattern  [[nil nil nil nil nil :█  nil nil nil nil nil]
                               [nil nil nil nil nil :█  nil nil nil nil nil]
                               [nil nil nil nil :█  nil nil nil nil nil nil]
                               [nil nil nil :█  nil nil nil :█  nil nil nil]
                               [:█  nil nil nil nil nil :█  nil nil nil :█ ]
                               [:█  :█  nil nil nil :█  nil nil nil :█  :█ ]
                               [:█  :█  nil nil nil :█  nil nil nil :█  :█ ]
                               [nil nil :█  nil nil :█  nil nil :█  nil nil]
                               [nil nil nil nil nil :█  nil nil nil nil nil]
                               [nil nil nil nil :█  nil nil nil nil nil nil]
                               [nil nil nil :█  nil nil nil :█  nil nil nil]
                               [:█  nil nil nil nil nil :█  nil nil nil :█ ]
                               [:█  :█  nil nil nil :█  nil nil nil :█  :█ ]])

(def my-first-standard-size-pattern
  ;; after the classic Dell Puzzler's Crossword Puzzles March 2015 #5 easy
  [[:E  :B  :B  :█  :S  :C  :A  :R  :█  nil nil nil nil :█  :█ ]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil :█ ]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil nil]
   [:█  :█  nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil :█  :█  :█ ]
   [nil nil nil nil nil nil nil :█  nil nil nil nil nil nil nil]
   [:█  :█  :█  nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil :█  :█ ]
   [nil nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [:█  nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [:█  :█  nil nil nil nil :█  nil nil nil nil :█  nil nil nil]])

(def standard-pattern-extra-black
  ;; again as Dell's March 2015 #5 but with extra black squares
  ;; inserted
  [[nil nil nil :█  :█  nil nil nil :█  nil nil nil :█  :█  :█ ]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil :█ ]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil :█ ]
   [:█  :█  nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [:█  nil nil nil nil :█  :█  nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil nil]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil :█  :█  :█ ]
   [nil nil nil nil nil nil nil :█  nil nil nil nil nil nil nil]
   [:█  :█  :█  nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil :█  :█  nil nil nil nil :█ ]
   [nil nil nil :█  nil nil nil nil :█  nil nil nil nil :█  :█ ]
   [:█  nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [:█  nil nil nil nil nil :█  nil nil nil nil :█  nil nil nil]
   [:█  :█  :█  nil nil nil :█  nil nil nil :█  :█  nil nil nil]])

(def another-standard-pattern
  ;; after Dell Puzzler's Crossword Puzzles March 2015 #1 easy
  [[nil nil nil :█  :█  :█  nil nil nil :█  :█  :█  nil nil nil]
   [nil nil nil nil :█  nil nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil nil nil nil nil :█  nil nil nil nil nil :█ ]
   [:█  nil nil nil nil nil nil :█  nil nil nil nil :█  :█  :█ ]
   [:█  :█  :█  nil nil nil :█  nil nil nil nil nil nil nil nil]
   [nil nil nil :█  :█  :█  nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil :█  :█  :█  nil nil nil]
   [nil nil nil nil nil nil nil nil :█  nil nil nil :█  :█  :█ ]
   [:█  :█  :█  nil nil nil nil :█  nil nil nil nil nil nil :█ ]
   [:█  nil nil nil nil nil :█  nil nil nil nil nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil nil :█  nil nil nil nil]
   [nil nil nil nil :█  nil nil nil nil nil :█  nil nil nil nil]
   [nil nil nil :█  :█  :█  nil nil nil :█  :█  :█  nil nil nil]])
