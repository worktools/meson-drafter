
(ns app.comp.form-previewer
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect <> >> list-> div button textarea span input pre code a]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-tabs comp-placeholder]]
            [app.generate :refer [generate-form-code]]
            ["copy-to-clipboard" :as copy!]))

(defcomp
 comp-snippet
 (text)
 (pre
  {:style (merge
           ui/expand
           {:font-family ui/font-code,
            :font-size 13,
            :line-height "20px",
            :padding "4px 12px 400px 12px",
            :background-color (hsl 0 0 98)})}
  (div
   {}
   (a
    {:inner-text "Copy",
     :style ui/link,
     :class-name "clickable",
     :on-click (fn [e d!] (copy! text))}))
  (code {:inner-text text})))

(defcomp
 comp-form-previewer
 (states fields)
 (let [cursor (:cursor states), state (or (:data states) {:page :gen})]
   (div
    {:style (merge ui/expand ui/column)}
    (comp-tabs
     {:selected (:page state)}
     [{:name :gen, :title "Generated"} {:name :json, :title "JSON config"}]
     (fn [info d!] (d! cursor (assoc state :page (:name info)))))
    (case (:page state)
      :json (comp-snippet (js/JSON.stringify (clj->js fields) nil 2))
      :gen (comp-snippet (generate-form-code fields {}))
      (<> (str "unknown page: " (:page state)))))))
