(ns styles.constants)

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
             :huge 60
             :x-huge 100
             :xx-huge 140
             :xxx-huge 180}
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
             :huge 90
             :x-huge 150
             :xx-huge 210
             :xxx-huge 270}
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
     :breakpoint {:small (+ width-small (* 2 (-> dimensions :spacing :x-small)))
                  :medium (+ width-medium (* 2 (-> dimensions :spacing :medium)))
                  :large (+ width-large (* 2 (-> dimensions :spacing :x-large)))}}))

