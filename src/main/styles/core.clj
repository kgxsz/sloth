(ns styles.core
  (:require [styles.constants :as c]
            [styles.components :as components]
            [styles.fonts :as fonts]
            [garden.def :refer [defstyles]]
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
   {:overflow :auto}]

  [:body
   {:overflow :auto}]

  ;; fonts
  fonts/icomoon

  ;; components
  components/icon
  components/text
  components/app-error-notice
  components/logo
  components/page
  components/user
  components/user-details
  components/calendar)
