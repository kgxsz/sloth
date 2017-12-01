(ns styles.utils
  (:require [styles.constants :as c]
            [garden.stylesheet :refer [at-media]]
            [garden.units :refer [px]]))

(defn tiny-width [styles]
  (at-media {:max-width (-> c/dimensions :breakpoint :tiny :end px)}
            [:& styles]))

(defn small-width [styles]
  (at-media {:min-width (-> c/dimensions :breakpoint :small :start px)
             :max-width (-> c/dimensions :breakpoint :small :end px)}
            [:& styles]))

(defn medium-width [styles]
  (at-media {:min-width (-> c/dimensions :breakpoint :medium :start px)
             :max-width (-> c/dimensions :breakpoint :medium :end px)}
            [:& styles]))

(defn large-width [styles]
  (at-media {:min-width (-> c/dimensions :breakpoint :large :start px)
             :max-width (-> c/dimensions :breakpoint :large :end px)}
            [:& styles]))

(defn huge-width [styles]
  (at-media {:min-width (-> c/dimensions :breakpoint :huge :start px)}
            [:& styles]))
