
(ns app.comp.live-demo
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect <> >> div button textarea span input create-element a]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-tabs comp-placeholder]]
            [app.comp.form-drafter :refer [comp-form-drafter]]
            [app.comp.form-previewer :refer [comp-form-previewer]]
            [app.generate :refer [generate-form-code]]
            [cumulo-util.core :refer [delay!]]))

(defn send-message! [fields url]
  (let [code (generate-form-code fields {})]
    (try
     (-> (js/document.querySelector "#demo-frame")
         .-contentWindow
         (.postMessage (clj->js {:type :items, :code code}) url))
     (catch js/Error error (do (js/console.log "failed to post" error))))))

(defeffect
 effect-post-data
 (fields url)
 (action el *local at-place?)
 (println action)
 (when (= action :update) (send-message! fields url)))

(defcomp
 comp-live-demo
 (states fields styles)
 (let [cursor (:cursor states)
       state (or (:data states) {:url "http://fe.jimu.io/meson-form/#/preview-mode"})]
   [(effect-post-data fields (:url state))
    (div
     {:style (merge ui/expand ui/column)}
     (div
      {:style ui/row}
      (input
       {:style (merge ui/expand ui/input),
        :value (:url state),
        :on-input (fn [e d!] (d! cursor (assoc state :url (:value e))))}))
     (create-element :iframe {:style ui/expand, :id "demo-frame", :src (:url state)}))]))
