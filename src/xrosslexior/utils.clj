(ns xrosslexior.utils)

(defmacro map-comprehension [bindings map-entry]
  `(into {} (for ~bindings ~map-entry)))
