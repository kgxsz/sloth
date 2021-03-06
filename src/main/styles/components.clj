(ns styles.components
  (:require [styles.constants :as c]
            [styles.utils :as u]
            [garden.def :refer [defstyles]]
            [garden.units :refer [px percent ms]]
            [normalize.core :refer [normalize]]))


(defstyles button
  [:.button
   {:width (-> c/filling :xx-huge px)
    :height (-> c/filling :xx-large px)
    :padding-left (-> c/spacing :medium px)
    :padding-right (-> c/spacing :medium px)
    :display :flex
    :align-items :center
    :justify-content :space-between
    :text-decoration :none
    :border-top :none
    :border-left :none
    :border-right :none
    :border-bottom :solid
    :border-width (-> c/filling :xx-tiny px)
    :border-top-left-radius (-> c/radius :large px)
    :border-top-right-radius (-> c/radius :large px)
    :cursor :pointer}

   [:&:focus
    {:outline :none}]

   [:&--disabled
    {:opacity 0.4
     :cursor :default}]

   [:&--border-color
    (u/make-modifiers c/colour :border-color)]

   [:&--background-color
    (u/make-modifiers c/colour :background-color)]

   [:&--margin-top
    (u/make-modifiers c/spacing :margin-top px)]

   [:&--margin-bottom
    (u/make-modifiers c/spacing :margin-bottom px)]])


(defstyles calendar
  [:.calendar
   {:margin-top (-> c/spacing :huge px)}

   [:&__header
    [:&__separator
     {:padding [[0 (-> c/spacing :x-small px)]]}]]

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
      :background-color (-> c/colour :white-light)}]]

   [:&__label
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
     [:&--colour
      (u/make-modifiers c/colour :background-color)]]]

   [:&__footer
    {:margin-top (-> c/spacing :x-large px)
     :border-bottom-style :solid
     :border-bottom-width (-> c/filling :xx-tiny px)
     :border-bottom-color (-> c/colour :grey-light)}]])


(defstyles icon
  [:.icon {:font-family "'icomoon'"
           :font-style :normal
           :font-weight 400
           :font-variant :normal
           :text-transform :none
           :-webkit-font-smoothing :antialiased
           :-moz-osx-font-smoothing :grayscale
           :text-decoration :none}

   [:&--font-size
    (u/make-modifiers c/font-size :font-size px)]

   [:&--colour
    (u/make-modifiers c/colour :color)]

   [:&--font-weight-bold {:font-weight 800}]

   [:&--question-circle:before {:content "'\\e87d'"}]
   [:&--arrow-left:before {:content "'\\e879'"}]
   [:&--moon:before {:content "'\\e808'"}]
   [:&--film-play:before {:content "'\\e824'"}]
   [:&--text-align-right:before {:content "'\\e89a'"}]
   [:&--google-plus:before {:content "'\\ea8b'"}]
   [:&--graduation-hat:before {:content "'\\e821'"}]
   [:&--printer:before {:content "'\\e81c'"}]
   [:&--dropbox:before {:content "'\\eaae'"}]
   [:&--pie-chart:before {:content "'\\e842'"}]
   [:&--bold:before {:content "'\\e893'"}]
   [:&--keyboard:before {:content "'\\e837'"}]
   [:&--paperclip:before {:content "'\\e819'"}]
   [:&--star-empty:before {:content "'\\e816'"}]
   [:&--bicycle:before {:content "'\\e850'"}]
   [:&--linkedin:before {:content "'\\eaca'"}]
   [:&--frame-contract:before {:content "'\\e88d'"}]
   [:&--tablet:before {:content "'\\e83b'"}]
   [:&--pointer-down:before {:content "'\\e8a8'"}]
   [:&--alarm:before {:content "'\\e858'"}]
   [:&--arrow-right:before {:content "'\\e87a'"}]
   [:&--volume-low:before {:content "'\\e85c'"}]
   [:&--envelope:before {:content "'\\e818'"}]
   [:&--github:before {:content "'\\eab0'"}]
   [:&--home:before {:content "'\\e800'"}]
   [:&--heart:before {:content "'\\e813'"}]
   [:&--youtube:before {:content "'\\ea9d'"}]
   [:&--chevron-left:before {:content "'\\e875'"}]
   [:&--inbox:before {:content "'\\e81a'"}]
   [:&--pointer-right:before {:content "'\\e8a7'"}]
   [:&--sad:before {:content "'\\e855'"}]
   [:&--bookmark:before {:content "'\\e829'"}]
   [:&--cart:before {:content "'\\e82e'"}]
   [:&--screen:before {:content "'\\e839'"}]
   [:&--car:before {:content "'\\e84e'"}]
   [:&--direction-rtl:before {:content "'\\e8a1'"}]
   [:&--pointer-left:before {:content "'\\e8a9'"}]
   [:&--thumbs-down:before {:content "'\\e86e'"}]
   [:&--facebook:before {:content "'\\ea90'"}]
   [:&--chevron-down:before {:content "'\\e874'"}]
   [:&--chevron-right:before {:content "'\\e876'"}]
   [:&--arrow-up:before {:content "'\\e877'"}]
   [:&--poop:before {:content "'\\e806'"}]
   [:&--store:before {:content "'\\e82d'"}]
   [:&--exit:before {:content "'\\e820'"}]
   [:&--indent-increase:before {:content "'\\e89d'"}]
   [:&--license:before {:content "'\\e822'"}]
   [:&--cloud-check:before {:content "'\\e80d'"}]
   [:&--construction:before {:content "'\\e841'"}]
   [:&--chevron-up-circle:before {:content "'\\e887'"}]
   [:&--cross-circle:before {:content "'\\e880'"}]
   [:&--chevron-down-circle:before {:content "'\\e888'"}]
   [:&--arrow-down:before {:content "'\\e878'"}]
   [:&--phone:before {:content "'\\e831'"}]
   [:&--page-break:before {:content "'\\e8a2'"}]
   [:&--highlight:before {:content "'\\e897'"}]
   [:&--redo:before {:content "'\\e861'"}]
   [:&--wheelchair:before {:content "'\\e851'"}]
   [:&--paypal:before {:content "'\\ead8'"}]
   [:&--eye:before {:content "'\\e81b'"}]
   [:&--twitter:before {:content "'\\ea96'"}]
   [:&--cloud:before {:content "'\\e809'"}]
   [:&--neutral:before {:content "'\\e856'"}]
   [:&--shirt:before {:content "'\\e82c'"}]
   [:&--flag:before {:content "'\\e817'"}]
   [:&--direction-ltr:before {:content "'\\e8a0'"}]
   [:&--vimeo:before {:content "'\\eaa0'"}]
   [:&--book:before {:content "'\\e828'"}]
   [:&--arrow-down-circle:before {:content "'\\e884'"}]
   [:&--drop:before {:content "'\\e804'"}]
   [:&--camera-video:before {:content "'\\e825'"}]
   [:&--move:before {:content "'\\e87b'"}]
   [:&--bubble:before {:content "'\\e83f'"}]
   [:&--power-switch:before {:content "'\\e83e'"}]
   [:&--history:before {:content "'\\e863'"}]
   [:&--cloud-sync:before {:content "'\\e80c'"}]
   [:&--leaf:before {:content "'\\e849'"}]
   [:&--pointer-up:before {:content "'\\e8a6'"}]
   [:&--text-align-left:before {:content "'\\e898'"}]
   [:&--menu-circle:before {:content "'\\e87e'"}]
   [:&--bug:before {:content "'\\e869'"}]
   [:&--chart-bars:before {:content "'\\e843'"}]
   [:&--sort-alpha-asc:before {:content "'\\e8a3'"}]
   [:&--gift:before {:content "'\\e844'"}]
   [:&--star:before {:content "'\\e814'"}]
   [:&--hand:before {:content "'\\e8a5'"}]
   [:&--music-note:before {:content "'\\e823'"}]
   [:&--calendar-full:before {:content "'\\e836'"}]
   [:&--chevron-up:before {:content "'\\e873'"}]
   [:&--volume-high:before {:content "'\\e85a'"}]
   [:&--briefcase:before {:content "'\\e84c'"}]
   [:&--hourglass:before {:content "'\\e85f'"}]
   [:&--mustache:before {:content "'\\e857'"}]
   [:&--sync:before {:content "'\\e862'"}]
   [:&--train:before {:content "'\\e84f'"}]
   [:&--magnifier:before {:content "'\\e86f'"}]
   [:&--indent-decrease:before {:content "'\\e89e'"}]
   [:&--underline:before {:content "'\\e895'"}]
   [:&--volume:before {:content "'\\e85d'"}]
   [:&--enter:before {:content "'\\e81f'"}]
   [:&--smartphone:before {:content "'\\e83a'"}]
   [:&--file-empty:before {:content "'\\e81d'"}]
   [:&--pencil:before {:content "'\\e802'"}]
   [:&--linearicons:before {:content "'\\e846'"}]
   [:&--text-format:before {:content "'\\e890'"}]
   [:&--enter-down:before {:content "'\\e867'"}]
   [:&--bullhorn:before {:content "'\\e859'"}]
   [:&--warning:before {:content "'\\e87c'"}]
   [:&--smile:before {:content "'\\e854'"}]
   [:&--line-spacing:before {:content "'\\e89c'"}]
   [:&--dinner:before {:content "'\\e847'"}]
   [:&--coffee-cup:before {:content "'\\e848'"}]
   [:&--undo:before {:content "'\\e860'"}]
   [:&--list:before {:content "'\\e872'"}]
   [:&--diamond:before {:content "'\\e845'"}]
   [:&--cloud-upload:before {:content "'\\e80a'"}]
   [:&--volume-medium:before {:content "'\\e85b'"}]
   [:&--checkmark-circle:before {:content "'\\e87f'"}]
   [:&--link:before {:content "'\\e86b'"}]
   [:&--text-format-remove:before {:content "'\\e891'"}]
   [:&--strikethrough:before {:content "'\\e896'"}]
   [:&--picture:before {:content "'\\e827'"}]
   [:&--laptop:before {:content "'\\e83c'"}]
   [:&--mic:before {:content "'\\e85e'"}]
   [:&--star-half:before {:content "'\\e815'"}]
   [:&--earth:before {:content "'\\e853'"}]
   [:&--exit-up:before {:content "'\\e868'"}]
   [:&--thumbs-up:before {:content "'\\e86d'"}]
   [:&--sun:before {:content "'\\e807'"}]
   [:&--code:before {:content "'\\e86a'"}]
   [:&--file-add:before {:content "'\\e81e'"}]
   [:&--frame-expand:before {:content "'\\e88c'"}]
   [:&--plus-circle:before {:content "'\\e881'"}]
   [:&--instagram:before {:content "'\\ea92'"}]
   [:&--camera:before {:content "'\\e826'"}]
   [:&--magic-wand:before {:content "'\\e803'"}]
   [:&--pilcrow:before {:content "'\\e89f'"}]
   [:&--database:before {:content "'\\e80e'"}]
   [:&--map-marker:before {:content "'\\e833'"}]
   [:&--apartment:before {:content "'\\e801'"}]
   [:&--arrow-right-circle:before {:content "'\\e886'"}]
   [:&--text-align-center:before {:content "'\\e899'"}]
   [:&--layers:before {:content "'\\e88e'"}]
   [:&--chevron-left-circle:before {:content "'\\e889'"}]
   [:&--spell-check:before {:content "'\\e838'"}]
   [:&--arrow-left-circle:before {:content "'\\e885'"}]
   [:&--cog:before {:content "'\\e810'"}]
   [:&--clock:before {:content "'\\e864'"}]
   [:&--google:before {:content "'\\ea88'"}]
   [:&--tag:before {:content "'\\e82f'"}]
   [:&--text-align-justify:before {:content "'\\e89b'"}]
   [:&--arrow-up-circle:before {:content "'\\e883'"}]
   [:&--funnel:before {:content "'\\e88f'"}]
   [:&--laptop-phone:before {:content "'\\e83d'"}]
   [:&--rocket:before {:content "'\\e84b'"}]
   [:&--dice:before {:content "'\\e812'"}]
   [:&--crop:before {:content "'\\e88b'"}]
   [:&--text-size:before {:content "'\\e892'"}]
   [:&--italic:before {:content "'\\e894'"}]
   [:&--circle-minus:before {:content "'\\e882'"}]
   [:&--location:before {:content "'\\e835'"}]
   [:&--cloud-download:before {:content "'\\e80b'"}]
   [:&--download:before {:content "'\\e865'"}]
   [:&--pushpin:before {:content "'\\e832'"}]
   [:&--lighter:before {:content "'\\e805'"}]
   [:&--user:before {:content "'\\e82a'"}]
   [:&--unlink:before {:content "'\\e86c'"}]
   [:&--heart-pulse:before {:content "'\\e840'"}]
   [:&--chevron-right-circle:before {:content "'\\e88a'"}]
   [:&--map:before {:content "'\\e834'"}]
   [:&--bus:before {:content "'\\e84d'"}]
   [:&--phone-handset:before {:content "'\\e830'"}]
   [:&--cross:before {:content "'\\e870'"}]
   [:&--trash:before {:content "'\\e811'"}]
   [:&--menu:before {:content "'\\e871'"}]
   [:&--upload:before {:content "'\\e866'"}]
   [:&--paw:before {:content "'\\e84a'"}]
   [:&--lock:before {:content "'\\e80f'"}]
   [:&--select:before {:content "'\\e852'"}]
   [:&--users:before {:content "'\\e82b'"}]
   [:&--sort-amount-asc:before {:content "'\\e8a4'"}]])


(defstyles logo
  [:.logo
   {:width (-> c/filling :xxx-large px)}
   [:&__square
    [:&--colour
     (u/make-modifiers c/colour :fill)]]])


(defstyles notification
  [:.notification
   {:display :flex
    :flex-direction :column
    :align-items :center
    :justify-content :center
    :height (-> c/filling :xxx-large px)
    :background-color (:yellow-light c/colour)
    :border-bottom :solid
    :border-color (:yellow-dark c/colour)
    :border-width (-> c/filling :xx-tiny px)
    :margin-bottom (-> c/spacing :xx-large px)}

   [:&__title
    {:display :flex
     :flex-direction :row
     :align-items :baseline
     :text-align :center}]

   [:&__paragraph
    {:display :flex
     :flex-direction :row
     :align-items :center
     :margin-top (-> c/spacing :xx-tiny px)}]])


(defstyles page
  [:.page

   (u/tiny-width
    {:display :none})

   [:&__header
    {:min-height (-> c/spacing :xxx-large px)}]

   [:&__body
    {:display :flex
     :flex-direction :column
     :align-items :center
     :min-width (-> c/breakpoint :small :start px)
     :overflow :auto
     :margin :auto}

    (u/small-width
     {:max-width (-> c/breakpoint :small :start px)})

    (u/medium-width
     {:max-width (-> c/breakpoint :medium :start px)})

    (u/large-width
     {:max-width (-> c/breakpoint :large :start px)})

    (u/huge-width
     {:max-width (-> c/breakpoint :huge :start px)})]

   [:&__footer
    {:min-height (-> c/spacing :xxx-large px)}]])


(defstyles sad-message
  [:.sad-message
   {:width (-> c/proportion :100 percent)
    :display :flex
    :flex-direction :column
    :align-items :center}])


(defstyles text
  [:.text {:font-family "Arial, \"Helvetica Neue\", Helvetica, sans-serif"
           :color (-> c/colour :black-light)
           :font-size (-> c/font-size :medium px)
           :font-weight 400
           :line-height 1.3
           :font-variant :normal
           :text-transform :none
           :-webkit-font-smoothing :antialiased
           :-moz-osx-font-smoothing :grayscale
           :text-decoration :none}

   [:&--padding-left
    (u/make-modifiers c/spacing :padding-left px)]

   [:&--padding-right
    (u/make-modifiers c/spacing :padding-right px)]

   [:&--margin-top
    (u/make-modifiers c/spacing :margin-top px)]

   [:&--margin-bottom
    (u/make-modifiers c/spacing :margin-bottom px)]

   [:&--font-size
    (u/make-modifiers c/font-size :font-size px)]

   [:&--colour
    (u/make-modifiers c/colour :color)]

   [:&--ellipsis {:display :block
                  :white-space :nowrap
                  :overflow :hidden
                  :text-overflow :ellipsis}]

   [:&--align-center {:text-align :center}]

   [:&--font-weight-bold {:font-weight 800}]])


(defstyles user
  [:.user
   (u/tiny-width
    {:width (-> c/calendar :width :small px)})

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
     :background-color (-> c/colour :grey-light)}]

   [:&__first-name
    {:margin-left (-> c/spacing :x-small px)
     :max-width (-> c/proportion :40 percent)}]

   [:&__divider
    {:flex-grow 1
     :height (-> c/filling :xx-tiny px)
     :margin-left (-> c/spacing :x-small px)
     :background-color (-> c/colour :grey-light)}]])











