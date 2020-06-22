
(ns app.updater
  (:require [respo.cursor :refer [update-states]]
            [medley.core :refer [remove-nth insert-nth]]))

(defn create-field [store data]
  (update
   store
   :fields
   (fn [fields]
     (case data
       :input (conj fields {:type :input, :label "TODO", :required? false, :name "TODO"})
       :textarea
         (conj fields {:type :textarea, :label "TODO", :required? false, :name "TODO"})
       :select
         (conj
          fields
          {:type :select,
           :options [{:value :todo, :display :TODO}],
           :label "TODO",
           :required? false,
           :name "TODO"})
       :decorative (conj fields {:type :decorative, :render nil})
       (do (println "unknown type" data) fields)))))

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
      (update
       store
       :fields
       (fn [fields]
         (println (remove-nth (first data) fields))
         (vec (remove-nth (first data) fields))))
    :else (do (println "unexpected data:" data) store)))

(defn updater [store op data op-id op-time]
  (case op
    :states (update-states store data)
    :create-field (create-field store data)
    :remove-field (remove-field store data)
    :drag-field (drag-field store data)
    :hydrate-storage data
    store))
