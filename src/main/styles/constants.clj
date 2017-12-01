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
   :breakpoint {:tiny {:end 319}
                :small {:start 320 :end 479}
                :medium {:start 480 :end 767}
                :large {:start 768 :end 1023}
                :huge {:start 1024}}
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

(def user-details
  {:height (-> dimensions :filling :medium px)})

(def calendar
  (let [item-width (-> dimensions :filling :x-small)
        item-gutter (-> dimensions :spacing :xx-tiny)
        label-width (-> dimensions :filling :large)
        weeks-to-width (fn [num-weeks]
                         (+ (* num-weeks item-width)
                            (* (dec num-weeks) item-gutter)
                            label-width))]

    {:width {:small (weeks-to-width 17)
             :medium (weeks-to-width 25)
             :large (weeks-to-width 43)
             :huge (weeks-to-width 52)}
     :item-width item-width
     :item-gutter item-gutter
     :label-width label-width}))
