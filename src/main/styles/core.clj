(ns styles.core
  (:require [styles.constants :as c]
            [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(defstyles app-error-notice
  [:.app-error-notice
   {:position :absolute
    :left 0
    :right 0
    :z-index -1
    :padding [[(-> c/dimensions :spacing :x-large px)
               (-> c/dimensions :spacing :x-small px)]]
    :text-align :center
    :color (-> c/colours :grey :dark)}])

(defstyles logo
  [:.logo
   {:width (-> c/dimensions :filling :xx-large px)
    :margin [[(-> c/dimensions :spacing :xx-huge px)
              :auto]]}
   [:&__square
    [:&--grey-medium
     {:fill (-> c/colours :grey :medium)}]
    [:&--grey-dark
     {:fill (-> c/colours :grey :dark)}]]])

(defstyles page
  [:.page
   {:display :none
    :background-color (-> c/colours :white :light)}

   (at-media
    {:min-width (-> c/page :breakpoint :small px)}
    [:&
     {:display :block
      :width (-> c/page :width :small px)
      :margin [[(-> c/dimensions :spacing :huge px)
                :auto]]}])

   (at-media
    {:min-width (-> c/page :breakpoint :medium px)}
    [:&
     {:width (-> c/page :width :medium px)}])

   (at-media
    {:min-width (-> c/page :breakpoint :large px)}
    [:&
     {:width (-> c/page :width :large px)}])])

(defstyles user-details
  [:.user-details
   {:display :flex
    :flex-direction :row
    :align-items :center
    :height (-> c/dimensions :filling :medium px)
    :width (percent 100)}

   [:&__avatar
    {:height (-> user :height px)
     :width (-> user :height px)
     :border-radius (percent 50)
     :background-color (-> c/colours :grey :light)}]

   [:&__first-name
    {:margin-left (-> c/dimensions :spacing :x-small px)
     :font-size (-> text :heading :medium px)
     :font-weight :bold
     :max-width (percent 40)
     :text-overflow :ellipsis
     :white-space :nowrap
     :overflow :hidden}]

   [:&__divider
    {:flex-grow 1
     :height (-> c/dimensions :filling :xx-tiny px)
     :margin-left (-> c/dimensions :spacing :x-small px)
     :background-color (-> c/colours :grey :light)}]])

(defstyles c/calendar
  [:.calendar
   {:margin-top (-> c/dimensions :spacing :huge px)}

   [:&__header
    [:&__title
     {:font-size (-> text :heading :medium px)
      :font-weight :bold
      :color (-> c/colours :black :light)}]
    [:&__separator
     {:padding [[0 (-> c/dimensions :spacing :x-small px)]]
      :font-size (-> text :heading :medium px)
      :font-weight :normal
      :color (-> c/colours :grey :dark)}]
    [:&__subtitle
     {:font-size (-> text :heading :medium px)
      :font-weight :normal
      :color (-> c/colours :grey :dark)}]]

   [:&__body
    {:position :relative
     :overflow :hidden
     :height (px (+ (* 7 (:item-width c/calendar))
                    (* 6 (:item-gutter c/calendar))
                    (:label-width c/calendar)))
     :width (percent 100)
     :margin-top (-> c/dimensions :spacing :x-large px)}]

   [:&__labels
    {:position :absolute
     :bottom 0}
    [:&--horizontal
     {:right 0
      :display :flex
      :flex-direction :row
      :height 0}]
    [:&--vertical
     {:top 0
      :left 0
      :width (-> c/calendar :label-width px)
      :background-color (-> c/colours :white :light)}]]

   [:&__label
    {:display :block
     :font-size (-> text :paragraph :small px)
     :font-weight :bold}
    [:&--hidden
     {:visibility :hidden}]
    [:&--horizontal
     {:height (px (+ (-> c/calendar :item-width) (-> c/calendar :item-gutter)))}]
    [:&--vertical
     {:width (px (+ (-> c/calendar :item-width) (-> c/calendar :item-gutter)))
      :transform "rotate(-90deg)"
      :transform-origin [[:left :top 0]]}]]

   [:&__items
    {:position :absolute
     :top 0
     :right 0
     :display :grid
     :grid-template-rows [(repeat 7 (-> c/calendar :item-width px))]
     :grid-auto-columns (-> c/calendar :item-width px)
     :grid-auto-flow :column
     :grid-gap (-> c/calendar :item-gutter px)}
    [:&__item
     {:border-radius (-> c/dimensions :radius :tiny px)
      :cursor :pointer}
     [:&--green
      {:background-color (-> c/colours :green :dark)}]
     [:&--yellow
      {:background-color (-> c/colours :yellow :dark)}]
     [:&--green
      {:background-color (-> c/colours :green :dark)}]
     [:&--purple
      {:background-color (-> c/colours :purple :dark)}]
     [:&--blue
      {:background-color (-> c/colours :blue :dark)}]
     [:&--grey-medium
      {:background-color (-> c/colours :grey :medium)}]
     [:&--grey-light
      {:background-color (-> c/colours :grey :light)}]]]

   [:&__footer
    {:margin-top (-> c/dimensions :spacing :x-large px)
     :border-bottom-style :solid
     :border-bottom-width (-> c/dimensions :filling :xx-tiny px)
     :border-bottom-color (-> c/colours :grey :light)}]])

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
    :color (-> c/colours :black :light)}])

(defstyles app
  normalize
  foundations
  app-error-notice
  logo
  page
  calendar)
