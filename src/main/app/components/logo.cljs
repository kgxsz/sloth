(ns app.components.logo
  (:require [om.dom :as dom]
            [om.next :as om :refer [defui]]))

(defui ^:once Logo
  Object
  (render
   [this]
   (dom/div
    #js {:className "logo"}
    (dom/svg
     #js {:xmlns "http://www.w3.org/2000/svg"
          :viewBox "0 0 64 42"}
     (dom/g
      nil
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-dark logo__square--animated-a"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(55 33)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-medium logo__square--animated-b"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(44 33)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-medium logo__square--animated-c"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(55 22)"})
      (dom/rect
       #js{:className "logo__square logo__square--colour-grey-dark logo__square--animated-d"
           :width "9"
           :height "9"
           :rx "1"
           :transform "translate(44 22)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-10.687-8.008h2.064l1.952,2.848a.351.351,0,0,0,.256.16h2.128c.085,0,.128-.038.128-.112a.4.4,0,0,0-.1-.208l-1.615-2.368a5.551,5.551,0,0,0-.512-.64v-.031a2.969,2.969,0,0,0,2.16-2.88,3.071,3.071,0,0,0-1.124-2.466,4.485,4.485,0,0,0-2.876-.894H24.328a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192H26.12a.208.208,0,0,0,.193-.192v-2.815Zm2.319-1.728H26.312v-3.04h2.319c1.214,0,1.968.589,1.968,1.536C30.6,1038.688,29.846,1039.264,28.632,1039.264Z"
           :transform "translate(4 -1007)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-medium"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-13.672-14.6a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192h4.08a5.6,5.6,0,0,0,3.9-1.314,4.61,4.61,0,0,0,1.38-3.486,4.513,4.513,0,0,0-1.516-3.5,5.711,5.711,0,0,0-3.8-1.3Zm4.08,7.776h-2.1v-5.952h2.065a2.9,2.9,0,0,1,3.136,2.976A2.769,2.769,0,0,1,27.408,1042.176Z"
           :transform "translate(-18 -1007)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-9.672-14.6a.208.208,0,0,0-.192.193v9.216a.208.208,0,0,0,.192.192H29.12a.208.208,0,0,0,.192-.192v-9.216a.208.208,0,0,0-.192-.193Z"
           :transform "translate(26 -1029)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-medium"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-10.687-8.008h2.064l1.952,2.848a.351.351,0,0,0,.256.16h2.128c.085,0,.128-.038.128-.112a.4.4,0,0,0-.1-.208l-1.615-2.368a5.551,5.551,0,0,0-.512-.64v-.031a2.969,2.969,0,0,0,2.16-2.88,3.071,3.071,0,0,0-1.124-2.466,4.485,4.485,0,0,0-2.876-.894H24.328a.205.205,0,0,0-.192.193v9.216a.2.2,0,0,0,.192.192H26.12a.208.208,0,0,0,.193-.192v-2.815Zm2.319-1.728H26.312v-3.04h2.319c1.214,0,1.968.589,1.968,1.536C30.6,1038.688,29.846,1039.264,28.632,1039.264Z"
           :transform "translate(4 -1029)"})
      (dom/path
       #js{:className "logo__square logo__square--colour-grey-dark"
           :d "M37,1049H19a1,1,0,0,1-1-1v-18a1,1,0,0,1,1-1H37a1,1,0,0,1,1,1v18A1,1,0,0,1,37,1049Zm-9.064-14.76a4.967,4.967,0,0,0-5.3,4.96,4.711,4.711,0,0,0,1.562,3.62,5.565,5.565,0,0,0,3.734,1.34,5.326,5.326,0,0,0,3.77-1.316,4.311,4.311,0,0,0,1.286-3.164v-.8h-5.76a.271.271,0,0,0-.224.224v1.216a.271.271,0,0,0,.224.224h3.536c-.061.891-1.055,1.792-2.832,1.792a3.185,3.185,0,0,1-2.178-.806,3.2,3.2,0,0,1,2.178-5.466,2.388,2.388,0,0,1,2.592,1.68.226.226,0,0,0,.24.16h1.648c.188,0,.256-.107.256-.208a3.254,3.254,0,0,0-1.138-2.262A5.206,5.206,0,0,0,27.936,1034.24Z"
           :transform "translate(-18 -1029)"}))))))

(def ui-logo (om/factory Logo))
