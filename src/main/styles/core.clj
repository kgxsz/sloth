(ns styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(def colours
  {:white {:light "#FFFFFF"}
   :black {:light "#333333"}
   :grey {:light "#F2F2F2"
          :medium "#EAEAEA"
          :dark "#BBBBBB"}
   :green {:dark "#8ACA55"}
   :yellow {:dark "#FADA6E"}
   :purple {:dark "#D4A3E3"}
   :blue {:dark "#58A1F5"}})

(def dimensions
  {:spacing {:tiny 1
             :xxx-small 2
             :xx-small 3
             :x-small 5
             :small 7
             :medium 10
             :large 15
             :x-large 30
             :xx-large 60
             :xxx-large 90
             :huge 120}
   :filling {:tiny 2
             :xx-small 10
             :x-small 14
             :small 20
             :medium 24
             :large 30}
   :radius {:tiny 1}})

(def text
  {:heading {:tiny 10
             :small 15
             :medium 20
             :large 25}
   :paragraph {:small 12
               :medium 14}})

(def calendar
  {:day-width (-> dimensions :filling :x-small)
   :day-gutter (-> dimensions :spacing :xxx-small)
   :label-width (-> dimensions :filling :large)})

(def user
  {:height (-> dimensions :filling :medium)})

(def page
  (let [weeks-to-width (fn [num-weeks]
                          (+ (* num-weeks (:day-width calendar))
                             (* (dec num-weeks) (:day-gutter calendar))
                             (:label-width calendar)))
        width-small (weeks-to-width 17)
        width-medium (weeks-to-width 32)
        width-large (weeks-to-width 52)]
    {:width {:small width-small
             :medium width-medium
             :large width-large}
     :breakpoint {:small (px (+ width-small (* 2 (-> dimensions :spacing :medium))))
                  :medium (px (+ width-medium (* 2 (-> dimensions :spacing :large))))
                  :large (px (+ width-large (* 2 (-> dimensions :spacing :x-large))))}}))

(defstyles app-error-notice
  [:.app-error-notice
   {:position :absolute
    :left 0
    :right 0
    :z-index -1
    :padding [[(-> dimensions :spacing :x-large px) (-> dimensions :spacing :medium px)]]
    :text-align :center
    :color (-> colours :grey :dark)}])

(defstyles page
  [:.page
   {:display :none
    :background-color (-> colours :white :light)}

   (at-media
    {:min-width (-> page :breakpoint :small px)}
    [:&
     {:display :block
      :width (-> page :width :small px)
      :margin [[(-> dimensions :spacing :xx-large px) :auto]]}])

   (at-media
    {:min-width (-> page :breakpoint :medium px)}
    [:&
     {:width (-> page :width :medium px)}])

   (at-media
    {:min-width (-> page :breakpoint :large px)}
    [:&
     {:width (-> page :width :large px)}])])

(defstyles user-details
  [:.user-details
   {:display :flex
    :flex-direction :row
    :align-items :center
    :height (-> user :height px)
    :width (percent 100)}

   [:&__avatar
    {:height (-> user :height px)
     :width (-> user :height px)
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

(defstyles calendar
  [:.calendar
   {:margin-top (-> dimensions :spacing :xx-large px)}

   [:&__header
    [:&__title
     {:font-size (-> text :heading :medium px)
      :font-weight :bold
      :color (-> colours :black :light)}]
    [:&__separator
     {:padding [[0 (-> dimensions :spacing :medium px)]]
      :font-size (-> text :heading :medium px)
      :font-weight :normal
      :color (-> colours :grey :dark)}]
    [:&__subtitle
     {:font-size (-> text :heading :medium px)
      :font-weight :normal
      :color (-> colours :grey :dark)}]]

   [:&__body
    {:position :relative
     :overflow :hidden
     :height (px (+ (* 7 (:day-width calendar))
                    (* 6 (:day-gutter calendar))
                    (:label-width calendar)))
     :width (percent 100)
     :margin-top (-> dimensions :spacing :x-large px)}]

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
      :width (-> calendar :label-width px)
      :background-color (-> colours :white :light)}]]

   [:&__label
    {:display :block
     :font-size (-> text :paragraph :small px)
     :font-weight :bold}
    [:&--hidden
     {:visibility :hidden}]
    [:&--horizontal
     {:height (px (+ (-> calendar :day-width) (-> calendar :day-gutter)))}]
    [:&--vertical
     {:width (px (+ (-> calendar :day-width) (-> calendar :day-gutter)))
      :transform "rotate(-90deg)"
      :transform-origin [[:left :top 0]]}]]

   [:&__items
    {:position :absolute
     :top 0
     :right 0
     :display :grid
     :grid-template-rows [(repeat 7 (-> calendar :day-width px))]
     :grid-auto-columns (-> calendar :day-width px)
     :grid-auto-flow :column
     :grid-gap (-> calendar :day-gutter px)}
    [:&__item
     {:border-radius (-> dimensions :radius :tiny px)
      :cursor :pointer}
     [:&--green
      {:background-color (-> colours :green :dark)}]
     [:&--yellow
      {:background-color (-> colours :yellow :dark)}]
     [:&--green
      {:background-color (-> colours :green :dark)}]
     [:&--purple
      {:background-color (-> colours :purple :dark)}]
     [:&--blue
      {:background-color (-> colours :blue :dark)}]
     [:&--grey-medium
      {:background-color (-> colours :grey :medium)}]
     [:&--grey-light
      {:background-color (-> colours :grey :light)}]]]

   [:&__footer
    {:margin-top (-> dimensions :spacing :x-large px)
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
  app-error-notice
  page
  user-details
  calendar)
