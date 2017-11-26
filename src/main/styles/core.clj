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
   :green {:light "#DEEAC5"
           :dark "#B0D082"}
   :yellow {:light "#FCECD1"
            :dark "#EDA443"}
   :pink {:light "#F5D6E5"
          :dark "#DB348D"}
   :purple {:light "#DDCEE1"
            :dark "#812A88"}
   :blue {:light "#CDE9FB"
          :dark "#73AFC6"}
   :violet {:light "#D3D3E6"
            :dark "#303A8C"}})

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
             :xx-small 10
             :x-small 14
             :small 20
             :medium 24
             :large 32}
   :radius {:tiny 1}
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

(def calendar
  {:day-width (-> dimensions :filling :x-small)
   :day-gutter (-> dimensions :spacing :xxx-small)
   :label-width (-> dimensions :filling :large)})

(def user
  {:height (-> dimensions :filling :medium)})

(def page
  (let [weeks-to-widths (fn [num-weeks]
                          (+ (* num-weeks (:day-width calendar))
                             (* (dec num-weeks) (:day-gutter calendar))
                             (:label-width calendar)))]
    {:width {:tiny (weeks-to-widths 17)
             :small (weeks-to-widths 27)
             :medium (weeks-to-widths 45)
             :large (weeks-to-widths 52)}}))

(defstyles app
  [:.app])

(defstyles app-error-notice
  [:.app-error-notice
   {:position :absolute
    :left 0
    :right 0
    :z-index -1
    :padding [[(-> dimensions :spacing :large px) (-> dimensions :spacing :medium px)]]
    :text-align :center
    :color (-> colours :grey :dark)}])

(defstyles page
  [:.page
   {:display :none
    :background-color (-> colours :white :light)}

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
   {:margin-top (-> dimensions :spacing :x-large px)}

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
     :margin-top (-> dimensions :spacing :large px)}]

   [:&__labels
    {:position :absolute
     :bottom 0}
    [:&--horizontal
     {:right 0
      :display :flex
      :flex-direction :row-reverse
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
     {:height (px (* 2 (+ (-> calendar :day-width) (-> calendar :day-gutter))))}]
    [:&--vertical
     {:width (px (+ (-> calendar :day-width) (-> calendar :day-gutter)))
      :transform "rotate(-90deg)"
      :transform-origin [[:left :top 0]]}]]

   [:&__days
    {:position :absolute
     :top 0
     :right 0
     :display :grid
     :grid-template-rows [(repeat 7 (-> calendar :day-width px))]
     :grid-auto-columns (-> calendar :day-width px)
     :grid-auto-flow :column
     :grid-gap (-> calendar :day-gutter px)}
    [:&__day
     {:border-radius (-> dimensions :radius :tiny px)
      :cursor :pointer}
     ;; TODO - play with the colours a little more here, and use shading transform on the month
     [:&--green
      {:background-color (-> colours :green :dark)}]
     [:&--yellow
      {:background-color (-> colours :yellow :dark)}]
     [:&--pink
      {:background-color (-> colours :pink :dark)}]
     [:&--purple
      {:background-color (-> colours :purple :dark)}]
     [:&--blue
      {:background-color (-> colours :blue :dark)}]
     [:&--violet
      {:background-color (-> colours :violet :dark)}]
     [:&--grey-medium
      {:background-color (-> colours :grey :medium)}]
     [:&--grey-light
      {:background-color (-> colours :grey :light)}]]]

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
  app-error-notice
  page
  user
  calendar)
