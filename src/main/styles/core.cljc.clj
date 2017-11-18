(ns styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(defstyles components
  [:.app
   {:width (px 950)
    :margin-top (px 100)
    :margin-bottom (px 100)
    :margin-left :auto
    :margin-right :auto}

   [:&__header
    {:width (percent 100)
     :height (px 170)}]

   [:&__body
    {:width (percent 100)
     :height (px 170)
     :background-color "#F0F0F0"}]]

  [:.user
   {:height (px 24)
    :display :flex
    :flex-direction :row}

   [:&__avatar
    {:width (px 24)
     :height (px 24)
     :border-radius (percent 50)}]

   [:&__first-name
    {:margin-left (px 10)
     :font-size (px 20)
     :line-height (px 24)
     :font-weight :bold}]

   [:&__divider
    {:flex-grow 1
     :margin-left (px 10)
     :margin-top (px 11)
     :margin-bottom (px 11)
     :border-top-color "#F2F2F2"
     :border-top-style :solid
     :border-top-width (px 2)}]

   [:&__options
    {:width (px 24)
     :height (px 24)
     :display :flex
     :flex-direction :row
     :align-items :center
     :justify-content :space-between
     :margin-left (px 10)}

    [:&__ellipse
     {:height (px 6)
      :width (px 6)
      :border-radius (percent 50)
      :background-color "#333"}]]])

(defstyles foundations
  [:*
   {:box-sizing :border-box
    :margin 0
    :padding 0}]
  [:body
   {:font-family "Arial, \"Helvetica Neue\", Helvetica, sans-serif"
    :font-size (px 12)
    :color "#333"}])

(defstyles app
  normalize
  foundations
  components)
