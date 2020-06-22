
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
            [respo-alerts.core :refer [use-modal-menu]]
            [cljs.reader :refer [read-string]]
            [feather.core :refer [comp-icon]]
            [medley.core :refer [find-first]]))

(def field-types
  [{:value :input, :display "Input"}
   {:value :textarea, :display "Textarea"}
   {:value :select, :display "Select"}
   {:value :decorative, :display "Decorative"}])

(defn render-field-type [kind]
  (let [target (find-first (fn [info] (= kind (:value info))) field-types)]
    (if (some? target) (:display target) (str kind))))

(defcomp
 comp-field-info
 (field path)
 (div
  {:style (merge
           ui/column
           {:background-color (hsl 0 0 98),
            :padding "2px 8px",
            :margin-bottom 8,
            :border (str "1px solid " (hsl 0 0 90))}),
   :draggable true,
   :on-dragstart (fn [e d!] (-> (:event e) .-dataTransfer (.setData "path" (pr-str path)))),
   :on-dragover (fn [e d!] (.preventDefault (:event e))),
   :on-drop (fn [e d!]
     (let [from (read-string (-> (:event e) .-dataTransfer (.getData "path")))]
       (d! :drag-field {:from from, :to path})))}
  (div
   {:style ui/row-parted}
   (<> (render-field-type (:type field)) {:font-family ui/font-fancy, :font-size 16})
   (comp-icon
    :x
    {:font-size 14, :color (hsl 0 80 70), :cursor :pointer}
    (fn [e d!] (d! :remove-field path))))))

(defcomp
 comp-form-drafter
 (states fields styles)
 (let [create-menu (use-modal-menu
                    (>> states :create)
                    {:title "Create field",
                     :style {},
                     :items field-types,
                     :on-result (fn [result d!] (d! :create-field (:value result)))})]
   (div
    {:style (merge ui/expand styles {:padding 16})}
    (div
     {}
     (list->
      {}
      (->> fields (map-indexed (fn [idx field] [idx (comp-field-info field [idx])]))))
     (div
      {:style (merge ui/center {:padding 8}), :onc nil}
      (a
       {:inner-text "Add", :style ui/link, :on-click (fn [e d!] ((:show create-menu) d!))})))
    (:ui create-menu))))
