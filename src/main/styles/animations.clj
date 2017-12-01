(ns styles.animations
  (:require [styles.constants :as c]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-keyframes]]))

(defstyles logo
  (at-keyframes :square-a
                [[:0% {:fill (:purple-dark c/colour)}]
                 [:10% {:fill (:purple-dark c/colour)}]
                 [:20% {:fill (:purple-dark c/colour)}]
                 [:30% {:fill (:grey-dark c/colour)}]
                 [:40% {:fill (:grey-dark c/colour)}]
                 [:50% {:fill (:blue-dark c/colour)}]
                 [:60% {:fill (:blue-dark c/colour)}]
                 [:70% {:fill (:green-dark c/colour)}]
                 [:80% {:fill (:green-dark c/colour)}]
                 [:90% {:fill (:yellow-dark c/colour)}]
                 [:100% {:fill (:grey-dark c/colour)}]])
  (at-keyframes :square-b
                [[:0% {:fill (:grey-medium c/colour)}]
                 [:10% {:fill (:grey-medium c/colour)}]
                 [:20% {:fill (:grey-medium c/colour)}]
                 [:30% {:fill (:yellow-dark c/colour)}]
                 [:40% {:fill (:blue-dark c/colour)}]
                 [:50% {:fill (:blue-dark c/colour)}]
                 [:60% {:fill (:purple-dark c/colour)}]
                 [:70% {:fill (:purple-dark c/colour)}]
                 [:80% {:fill (:green-dark c/colour)}]
                 [:90% {:fill (:green-dark c/colour)}]
                 [:100% {:fill (:grey-medium c/colour)}]])
  (at-keyframes :square-c
                [[:0% {:fill (:yellow-dark c/colour)}]
                 [:10% {:fill (:yellow-dark c/colour)}]
                 [:20% {:fill (:yellow-dark c/colour)}]
                 [:30% {:fill (:purple-dark c/colour)}]
                 [:40% {:fill (:purple-dark c/colour)}]
                 [:50% {:fill (:blue-dark c/colour)}]
                 [:60% {:fill (:blue-dark c/colour)}]
                 [:70% {:fill (:grey-medium c/colour)}]
                 [:80% {:fill (:grey-medium c/colour)}]
                 [:90% {:fill (:green-dark c/colour)}]
                 [:100% {:fill (:grey-medium c/colour)}]])
  (at-keyframes :square-d
                [[:0% {:fill (:green-dark c/colour)}]
                 [:10% {:fill (:green-dark c/colour)}]
                 [:20% {:fill (:green-dark c/colour)}]
                 [:30% {:fill (:grey-dark c/colour)}]
                 [:40% {:fill (:blue-dark c/colour)}]
                 [:50% {:fill (:blue-dark c/colour)}]
                 [:60% {:fill (:purple-dark c/colour)}]
                 [:70% {:fill (:purple-dark c/colour)}]
                 [:80% {:fill (:yellow-dark c/colour)}]
                 [:90% {:fill (:yellow-dark c/colour)}]
                 [:100% {:fill (:grey-dark c/colour)}]]))
