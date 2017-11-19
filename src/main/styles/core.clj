(ns styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(def colours
  {:grey {:light "#F2F2F2"}})

(def dimensions
  {:spacing {:tiny 1
             :small 5
             :medium 10
             :large 50
             :huge 100}
   :filling {:tiny 2}})

(def text
  {:heading {:small 15
             :medium 20
             :large 25}})

(def app
  {:width {:large 862}})

(def user
  {:height {:medium 24}})

(defstyles components
  [:.app
   {:display :flex
    :flex-direction :column
    :align-items :center
    :width (-> app :width :large px)
    :margin-top (-> dimensions :spacing :huge px)
    :margin-bottom (-> dimensions :spacing :huge px)
    :margin-left :auto
    :margin-right :auto
    :background-color :green}]

  [:.user
   {:display :flex
    :flex-direction :row
    :align-items :center
    :height (-> user :height :medium px)
    :width (percent 100)
    :background-color :pink}

   [:&__avatar
    {:height (-> user :height :medium px)
     :width (-> user :height :medium px)
     :border-radius (percent 50)
     :background-color (-> colours :grey :light)}]

   [:&__first-name
    {:margin-left (-> dimensions :spacing :medium px)
     :font-size (-> text :heading :medium px)
     :font-weight :bold}]

   [:&__divider
    {:flex-grow 1
     :height (-> dimensions :filling :tiny px)
     :margin-left (-> dimensions :spacing :medium px)
     :background-color (-> colours :grey :light)}]])

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
