(ns styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(def colours
  {:white {:light "#FFFFFF"}
   :black {:light "#333333"}
   :grey {:light "#F2F2F2"
          :dark "#BBBBBB"}})

(def dimensions
  {:spacing {:tiny 1
             :xxx-small 2
             :xx-small 3
             :x-small 4
             :small 5
             :medium 10
             :large 30
             :x-large 60
             :huge 100}
   :filling {:tiny 2
             :small 20
             :medium 40}
   :breakpoint {:tiny 320
                :small 480
                :medium 768
                :large 960}})

(def text
  {:heading {:tiny 10
             :small 15
             :medium 20
             :large 25}
   :paragraph {:small 12
               :medium 14}})

(def page
  {:width {:tiny 300
           :small 460
           :medium 700
           :large 862}})

(def user
  {:height {:medium 24}})

(defstyles app
  [:.app])

(defstyles notice
  [:.notice
   {:padding [[(-> dimensions :spacing :large px) (-> dimensions :spacing :medium px)]]
    :text-align :center
    :color (-> colours :grey :dark)}

   [:&--hidden
    (at-media
     {:min-width (-> dimensions :breakpoint :tiny px)}
     [:&
      {:display :none}])]])

(defstyles page
  [:.page
   {:display :none}

   (at-media
    {:min-width (-> dimensions :breakpoint :tiny px)}
    [:&
     {:display :block
      :width (-> page :width :tiny px)
      :margin [[(-> dimensions :spacing :x-large px) :auto]]}])

   (at-media
    {:min-width (-> dimensions :breakpoint :small px)}
    [:&
     {:width (-> page :width :small px)}])

   (at-media
    {:min-width (-> dimensions :breakpoint :medium px)}
    [:&
     {:width (-> page :width :medium px)}])

   (at-media
    {:min-width (-> dimensions :breakpoint :large px)}
    [:&
     {:width (-> page :width :large px)}])])

(defstyles user
  [:.user
   {:display :flex
    :flex-direction :row
    :align-items :center
    :height (-> user :height :medium px)
    :width (percent 100)}

   [:&__avatar
    {:height (-> user :height :medium px)
     :width (-> user :height :medium px)
     :border-radius (percent 50)
     :background-color (-> colours :grey :light)}]

   [:&__first-name
    {:margin-left (-> dimensions :spacing :medium px)
     :font-size (-> text :heading :medium px)
     :font-weight :bold
     :max-width (percent 40)
     :text-overflow :ellipsis
     :white-space :nowrap
     :overflow :hidden}]

   [:&__divider
    {:flex-grow 1
     :height (-> dimensions :filling :tiny px)
     :margin-left (-> dimensions :spacing :medium px)
     :background-color (-> colours :grey :light)}]])

(defstyles task
  [:.task
   {:margin-top (-> dimensions :spacing :x-large px)}

   [:&__header
    {:font-size (-> text :heading :medium px)}

    [:&__title
     {:font-weight :bold}]

    [:&__divider
     {:margin [[0 (-> dimensions :spacing :medium px)]]
      :color (-> colours :grey :dark)}]

    [:&__subtitle
     {:color (-> colours :grey :dark)}]]

   [:&__body
    {:display :flex
     :height (px 130)
     :margin-top (-> dimensions :spacing :large px)}

    [:&__section-left
     {:width (px 32)
      :min-width (px 32)}]

    [:&__section-right
     {:position :relative
      :overflow :hidden
      :width (percent 100)}]]

   [:&__day-label
    {:display :block
     :height (px 32)
     :font-size (-> text :paragraph :small px)
     :font-weight :bold}]

   [:&__days
    {:display :grid
     :grid-template-columns [(repeat 52 (px 14))]
     :grid-auto-rows (px 14)
     :grid-gap (px 2)
     :position :absolute
     :top 0
     :right 0
     :width (px 830)}
    [:&__day
     {:border-radius (px 1)
      :background-color (-> colours :grey :light)}]]

   [:&__month-labels
    {:position :absolute
     :bottom 0
     :right 0
     :width (px 830)
     :height (px 20)
     }
    [:&__month-label
     {:display :inline-block
      :width (px 64)
      :font-size (-> text :paragraph :small px)
      :font-weight :bold}]]

   [:&__footer
    {:margin-top (-> dimensions :spacing :large px)
     :border-bottom-style :solid
     :border-bottom-width (-> dimensions :filling :tiny px)
     :border-bottom-color (-> colours :grey :light)}]])

(defstyles foundations
  [:*
   {:box-sizing :border-box
    :margin 0
    :padding 0}]

  [:html
   {:overflow :auto
    :height (percent 100)}]

  [:body
   {:overflow :auto
    :height (percent 100)
    :font-family "Arial, \"Helvetica Neue\", Helvetica, sans-serif"
    :font-size (-> text :paragraph :medium px)
    :line-height 1.3
    :color (-> colours :black :light)}])

(defstyles app
  normalize
  foundations
  app
  notice
  page
  user
  task)
