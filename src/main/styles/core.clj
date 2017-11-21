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

(def task
  (let [day-width (-> dimensions :filling :x-small)
        day-gutter (-> dimensions :spacing :xxx-small)]
    {:day-width day-width
     :day-gutter day-gutter
     :label-container-width (-> dimensions :filling :large)
     :day-label-container-height (* 2 (+ day-width day-gutter))
     :days-width (+ (* 52 day-width) (* 51 day-gutter))
     :days-height (+ (* 7 day-width) (* 6 day-gutter))}))

(def user
  {:height 24})

(def page
  (let [weeks-to-widths (fn [num-weeks]
                          (+ (* num-weeks (:day-width task))
                             (* (dec num-weeks) (:day-gutter task))
                             (:label-container-width task)))]
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

(defstyles task
  [:.task
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

   [:&__calendar
    {:display :flex
     :height (px (+ (:days-height task) (:label-container-width task)))
     :margin-top (-> dimensions :spacing :large px)}

    [:&__section-left
     {:width (-> task :label-container-width px)
      :min-width (-> task :label-container-width px)}]

    [:&__section-right
     {:position :relative
      :overflow :hidden
      :width (percent 100)}
     [:&__insulator
      {:position :absolute
       :right 0
       :width (-> task :days-width px)}]]

    [:&__label
     {:display :block
      :font-size (-> text :paragraph :small px)
      :font-weight :bold}
     [:&--vertical
      {:transform "rotate(-90deg)"
       :transform-origin [[:left :top 0]]}]]]

   [:&__day-label-container
    {:height (-> task :day-label-container-height px)}]

   [:&__days
    {:display :grid
     :grid-template-columns [(repeat 52 (-> task :day-width px))]
     :grid-auto-rows (-> task :day-width px)
     :grid-gap (-> task :day-gutter px)}
    [:&__day
     {:border-radius (-> dimensions :radius :tiny px)
      :background-color (-> colours :grey :light)}]]

   [:&__month-labels
    {:display :flex}
    [:&__month-label-container
     {:position :relative
      :top (-> task :label-container-width px)
      :height 0
      :width (px 64)  ;; TODO - this is decided by the application
      }]]

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
  task)
