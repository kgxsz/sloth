(ns styles.core
  (:require [styles.animations :as animations]
            [styles.components :as components]
            [styles.constants :as c]
            [styles.fonts :as fonts]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-keyframes]]
            [garden.units :refer [px percent ms]]
            [normalize.core :refer [normalize]]))

(defstyles app

  ;; third party css
  normalize

  ;; foundations
  [:*
   {:box-sizing :border-box
    :margin 0
    :padding 0}]

  [:html
   {:overflow :auto
    :height (-> c/proportion :100 percent)}]

  [:body
   {:overflow-x :hidden
    :overflow-y :auto
    :height (-> c/proportion :100 percent)}]

  ;; fonts
  fonts/icomoon

  ;; animations
  animations/logo

  ;; components
  components/icon
  components/text
  components/logo
  components/notification
  components/page
  components/user
  components/user-details
  components/calendar)
