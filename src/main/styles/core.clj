(ns styles.core
  (:require [garden.def :refer [defstyles]]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px percent]]
            [normalize.core :refer [normalize]]))

(def colours
  {:black {:light "#333333"}
   :grey {:light "#F2F2F2"
          :dark "#999999"}})

(def dimensions
  {:spacing {:tiny 1
             :small 5
             :medium 10
             :large 50
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
   :paragraph {:medium 14}})

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
      :margin [[(-> dimensions :spacing :large px) :auto]]}])

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

(defstyles grid
  [:.grid
   {:margin-top (-> dimensions :spacing :large px)}
   [:&__header
    {:height (-> dimensions :filling :medium px)}

    [:&__title
     {:font-size (-> text :heading :medium px)
      :font-weight :bold
      :max-width (percent 30)
      :text-overflow :ellipsis
      :white-space :nowrap
      :overflow :hidden}]

    [:&__divider
     {:margin [[0 (-> dimensions :spacing :medium px)]]
      :font-size (-> text :heading :medium px)
      :color (-> colours :grey :dark)}]

    [:&__subtitle
     {:font-size (-> text :heading :medium px)
      :color (-> colours :grey :dark)
      :max-width (percent 30)
      :text-overflow :ellipsis
      :white-space :nowrap
      :overflow :hidden}]]])

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
    :color (-> colours :black :light)}])

(defstyles app
  normalize
  foundations
  app
  notice
  page
  user
  grid)
