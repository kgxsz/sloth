(ns styles.core
  (:require [styles.constants :as c]
            [styles.utils :as u]
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
    :padding [[(-> c/spacing :x-large px)
               (-> c/spacing :x-small px)]]
    :text-align :center
    :color (-> c/colours :grey :dark)}])

(defstyles logo
  [:.logo
   {:width (-> c/filling :xx-large px)}
   [:&__square
    [:&--grey-medium
     {:fill (-> c/colours :grey :medium)}]
    [:&--grey-dark
     {:fill (-> c/colours :grey :dark)}]]])

(defstyles page
  [:.page
   {:display :flex
    :flex-direction :column
    :align-items :center
    :margin :auto
    :background-color (-> c/colours :white :light)}

   (u/tiny-width
    {:display :none})

   (u/small-width
    {:max-width (-> c/breakpoint :small :start px)
     :padding [[(-> c/spacing :xx-large px)
                0]]})

   (u/medium-width
    {:max-width (-> c/breakpoint :medium :start px)
     :padding [[(-> c/spacing :xx-large px)
                (-> c/spacing :large px)]]})

   (u/large-width
    {:max-width (-> c/breakpoint :large :start px)
     :padding [[(-> c/spacing :huge px)
                (-> c/spacing :large px)]]})

   (u/huge-width
    {:max-width (-> c/breakpoint :huge :start px)
     :padding [[(-> c/spacing :huge px)
                 (-> c/spacing :large px)]]})])

(defstyles user
  [:.user
   (u/small-width
    {:width (-> c/calendar :width :small px)})

   (u/medium-width
    {:width (-> c/calendar :width :medium px)})

   (u/large-width
    {:width (-> c/calendar :width :large px)})

   (u/huge-width
    {:width (-> c/calendar :width :huge px)})])

(defstyles user-details
  [:.user-details
   {:display :flex
    :flex-direction :row
    :align-items :center
    :height (-> c/user-details :height px)}

   [:&__avatar
    {:height (-> c/user-details :height px)
     :width (-> c/user-details :height px)
     :border-radius (-> c/proportion :50 percent)
     :background-color (-> c/colours :grey :light)}]

   [:&__first-name
    {:margin-left (-> c/spacing :x-small px)
     :font-size (-> text :heading :medium px)
     :font-weight :bold
     :max-width (-> c/proportion :40 percent)
     :text-overflow :ellipsis
     :white-space :nowrap
     :overflow :hidden}]

   [:&__divider
    {:flex-grow 1
     :height (-> c/filling :xx-tiny px)
     :margin-left (-> c/spacing :x-small px)
     :background-color (-> c/colours :grey :light)}]])

(defstyles calendar
  [:.calendar
   {:margin-top (-> c/spacing :huge px)}

   [:&__header
    [:&__title
     {:font-size (-> c/heading :medium px)
      :font-weight :bold
      :color (-> c/colours :black :light)}]
    [:&__separator
     {:padding [[0 (-> c/spacing :x-small px)]]
      :font-size (-> c/heading :medium px)
      :font-weight :normal
      :color (-> c/colours :grey :dark)}]
    [:&__subtitle
     {:font-size (-> c/heading :medium px)
      :font-weight :normal
      :color (-> c/colours :grey :dark)}]]

   [:&__body
    {:position :relative
     :overflow :hidden
     :width (-> c/proportion :100 percent)
     :height (px (+ (* 7 (:item-width c/calendar))
                    (* 6 (:item-gutter c/calendar))
                    (:label-width c/calendar)))
     :margin-top (-> c/spacing :x-large px)}]

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
     :font-size (-> c/paragraph :small px)
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
     {:border-radius (-> c/radius :tiny px)
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
    {:margin-top (-> c/spacing :x-large px)
     :border-bottom-style :solid
     :border-bottom-width (-> c/filling :xx-tiny px)
     :border-bottom-color (-> c/colours :grey :light)}]])

(defstyles foundations
  [:*
   {:box-sizing :border-box
    :margin 0
    :padding 0}]

  [:html
   {:overflow :auto
    :height (-> c/proportion :100 percent)}]

  [:body
   {:overflow :auto
    :height (-> c/proportion :100 percent)
    :font-family "Arial, \"Helvetica Neue\", Helvetica, sans-serif"
    :font-size (-> c/paragraph :medium px)
    :line-height 1.3
    :color (-> c/colours :black :light)}])

(defstyles app
  normalize
  foundations
  app-error-notice
  logo
  page
  user
  user-details
  calendar)
