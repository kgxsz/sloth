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
  {:spacing {:xxx-tiny 1
             :xx-tiny 2
             :x-tiny 3
             :tiny 4
             :xxx-small 6
             :xx-small 8
             :x-small 10
             :small 12
             :medium 16
             :large 20
             :x-large 30
             :xx-large 40
             :xxx-large 50
             :huge 60}
   :filling {:xxx-tiny 1
             :xx-tiny 2
             :x-tiny 3
             :tiny 4
             :xxx-small 6
             :xx-small 10
             :x-small 14
             :small 18
             :medium 24
             :large 30
             :x-large 50
             :xx-large 70
             :huge 90}
   :radius {:tiny 1
            :small 2
            :medium 3
            :large 4
            :huge 5}})

(def text
  {:heading {:tiny 10
             :small 14
             :medium 20
             :large 28
             :huge 36}
   :paragraph {:tiny 10
               :small 12
               :medium 14
               :large 16
               :huge 18}})

(def calendar
  {:item-width (-> dimensions :filling :x-small)
   :item-gutter (-> dimensions :spacing :xx-tiny)
   :label-width (-> dimensions :filling :large)})

(def user
  {:height (-> dimensions :filling :medium)})

(def page
  (let [weeks-to-width (fn [num-weeks]
                          (+ (* num-weeks (:item-width calendar))
                             (* (dec num-weeks) (:item-gutter calendar))
                             (:label-width calendar)))
        width-small (weeks-to-width 17)
        width-medium (weeks-to-width 32)
        width-large (weeks-to-width 52)]
    {:width {:small width-small
             :medium width-medium
             :large width-large}
     :breakpoint {:small (px (+ width-small (* 2 (-> dimensions :spacing :x-small))))
                  :medium (px (+ width-medium (* 2 (-> dimensions :spacing :medium))))
                  :large (px (+ width-large (* 2 (-> dimensions :spacing :x-large))))}}))

(defstyles app-error-notice
  [:.app-error-notice
   {:position :absolute
    :left 0
    :right 0
    :z-index -1
    :padding [[(-> dimensions :spacing :x-large px) (-> dimensions :spacing :x-small px)]]
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
      :margin [[(-> dimensions :spacing :huge px) :auto]]}])

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
    {:margin-left (-> dimensions :spacing :x-small px)
     :font-size (-> text :heading :medium px)
     :font-weight :bold
     :max-width (percent 40)
     :text-overflow :ellipsis
     :white-space :nowrap
     :overflow :hidden}]

   [:&__divider
    {:flex-grow 1
     :height (-> dimensions :filling :xx-tiny px)
     :margin-left (-> dimensions :spacing :x-small px)
     :background-color (-> colours :grey :light)}]])

(defstyles calendar
  [:.calendar
   {:margin-top (-> dimensions :spacing :huge px)}

   [:&__header
    [:&__title
     {:font-size (-> text :heading :medium px)
      :font-weight :bold
      :color (-> colours :black :light)}]
    [:&__separator
     {:padding [[0 (-> dimensions :spacing :x-small px)]]
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
     :height (px (+ (* 7 (:item-width calendar))
                    (* 6 (:item-gutter calendar))
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
     {:height (px (+ (-> calendar :item-width) (-> calendar :item-gutter)))}]
    [:&--vertical
     {:width (px (+ (-> calendar :item-width) (-> calendar :item-gutter)))
      :transform "rotate(-90deg)"
      :transform-origin [[:left :top 0]]}]]

   [:&__items
    {:position :absolute
     :top 0
     :right 0
     :display :grid
     :grid-template-rows [(repeat 7 (-> calendar :item-width px))]
     :grid-auto-columns (-> calendar :item-width px)
     :grid-auto-flow :column
     :grid-gap (-> calendar :item-gutter px)}
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
     :border-bottom-width (-> dimensions :filling :xx-tiny px)
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
