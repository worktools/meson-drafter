
(ns app.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core :refer [defcomp defeffect <> >> div button textarea span input]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-tabs comp-placeholder]]
            [app.comp.form-drafter :refer [comp-form-drafter]]
            [app.comp.form-previewer :refer [comp-form-previewer]]
            [app.comp.live-demo :refer [comp-live-demo]]))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       cursor (or (:cursor states) [])
       state (or (:data states) {:page :form})]
   (div
    {:style (merge ui/fullscreen ui/global ui/row)}
    (comp-tabs
     {:selected (:page state),
      :width 80,
      :style {:border-right (str "1px solid " (hsl 0 0 90))}}
     [{:name :form, :title "Form"}]
     (fn [info d!] ))
    (comp-form-previewer (>> states :previewer) (or (:fields store) []))
    (comp-live-demo
     (>> states :demo)
     (:fields store)
     {:border-left (str "1px solid " (hsl 0 0 90))})
    (comp-form-drafter
     (>> states :form)
     (:fields store)
     {:border-left (str "1px solid " (hsl 0 0 90))})
    (when dev? (comp-reel (>> states :reel) reel {})))))
