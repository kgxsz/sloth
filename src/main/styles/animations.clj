(ns styles.animations
  (:require [styles.constants :as c]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-keyframes]]))

(defstyles logo
  (at-keyframes :square-a
                [[:0% {:fill (:grey-dark c/colour)}]
                 [:20% {:fill (:blue-dark c/colour)}]
                 [:40% {:fill (:grey-dark c/colour)}]
                 [:80% {:fill (:blue-dark c/colour)}]])
  (at-keyframes :square-b
                [[:0% {:fill (:grey-medium c/colour)}]
                 [:20% {:fill (:yellow-dark c/colour)}]
                 [:40% {:fill (:grey-medium c/colour)}]
                 [:80% {:fill (:yellow-dark c/colour)}]])
  (at-keyframes :square-c
                [[:0% {:fill (:grey-medium c/colour)}]
                 [:20% {:fill (:purple-dark c/colour)}]
                 [:40% {:fill (:grey-medium c/colour)}]
                 [:80% {:fill (:purple-dark c/colour)}]])
  (at-keyframes :square-d
                [[:0% {:fill (:grey-dark c/colour)}]
                 [:20% {:fill (:green-dark c/colour)}]
                 [:40% {:fill (:grey-dark c/colour)}]
                 [:80% {:fill (:green-dark c/colour)}]]))
