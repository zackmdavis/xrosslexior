;; XXX: this "eval" hack seems to actually affect all clojure-mode
;; buffers in the Emacs session
((clojure-mode . ((eval . (put-clojure-indent 'map-comprehension 1)))))
