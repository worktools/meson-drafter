
(ns app.updater
  (:require [respo.cursor :refer [update-states]]
            [medley.core :refer [remove-nth insert-nth]]))

(defn expand-field [data op-id]
  (case data
    :input {:type :input, :label (str "TODO_" op-id), :required? false, :name "TODO"}
    :textarea {:type :textarea, :label "TODO", :required? false, :name (str "TODO_" op-id)}
    :number {:type :number, :label "TODO", :required? false, :name (str "TODO_" op-id)}
    :select
      {:type :select,
       :options [{:value :todo, :display :TODO}],
       :label "TODO",
       :required? false,
       :name (str "TODO_" op-id)}
    :decorative {:type :decorative, :render nil}
    :custom
      {:type :custom, :label "TODO", :required? false, :name (str "TODO_" op-id), :render nil}
    :group {:type :group, :children []}
    (do (println "unknown type for field" data) nil)))

(defn create-field [store data op-id op-time]
  (update
   store
   :fields
   (fn [fields]
     (let [field-data (expand-field data op-id)]
       (if (some? field-data) (conj fields field-data) fields)))))

(defn drag-field [store data]
  (update
   store
   :fields
   (fn [fields]
     (cond
       (and (= 1 (count (:from data))) (= 1 (count (:to data))))
         (let [from (first (:from data)), to (first (:to data))]
           (->> fields (remove-nth from) (insert-nth to (nth fields from)) (vec)))
       :else (do (println "Unknown data:" data) fields)))))

(defn remove-field [store data]
  (cond
    (= 1 (count data))
      (update store :fields (fn [fields] (vec (remove-nth (first data) fields))))
    :else (do (println "unexpected data:" data) store)))

(defn updater [store op data op-id op-time]
  (case op
    :states (update-states store data)
    :create-field (create-field store data op-id op-time)
    :remove-field (remove-field store data)
    :drag-field (drag-field store data)
    :hydrate-storage data
    store))
