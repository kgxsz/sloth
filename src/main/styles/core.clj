(ns styles.core
  (:require [garden.def :refer [defstylesheet defstyles]]
            [garden.units :refer [px]]
            [normalize.core :refer [normalize]]))

(defstyles app
  normalize
  [:body
   {:font-family "sans-serif"
    :font-size (px 16)
    :line-height 1.5}]
  [:app
   {:width (px 100)
    :height (px 100)
    :background-color :red}])
