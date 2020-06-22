
(ns app.comp.form-drafter
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect <> >> list-> div button textarea span input a]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [app.config :refer [dev?]]
            [respo-ui.comp :refer [comp-tabs comp-placeholder]]
            [respo-alerts.core :refer [use-modal-menu]]))

(defcomp
 comp-form-drafter
 (states fields styles)
 (let [create-menu (use-modal-menu
                    (>> states :create)
                    {:title "Create field",
                     :style {},
                     :items [{:value :input, :display "Input"}
                             {:value :textarea, :display "Textarea"}
                             {:value :select, :display "Select"}
                             {:value :decorative, :display "Decorative"}],
                     :on-result (fn [result d!] (d! :create-field (:value result)))})]
   (div
    {:style (merge ui/expand styles)}
    (div
     {}
     (list->
      {}
      (->> fields (map-indexed (fn [idx field] [idx (div {} (<> (pr-str field)))]))))
     (div
      {:style (merge ui/center {:padding 8}), :onc nil}
      (a
       {:inner-text "Add", :style ui/link, :on-click (fn [e d!] ((:show create-menu) d!))})))
    (:ui create-menu))))
