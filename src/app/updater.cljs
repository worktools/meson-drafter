
(ns app.updater
  (:require [respo.cursor :refer [update-states]]
            [medley.core :refer [remove-nth insert-nth]]))

(defn contains-branch? [from to]
  (cond
    (and (empty? from) (not (empty? to))) true
    (= (first from) (first to)) (recur (rest from) (rest to))
    :else false))

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
    :nested {:type :nested, :children []}
    (do (println "unknown type for field" data) nil)))

(defn create-field [store data op-id op-time]
  (let [field-type (:type data)
        path (:path data)
        data-path (->> (:path data) (mapcat (fn [x] [x :children])))]
    (update-in
     store
     (concat [:fields] data-path)
     (fn [fields]
       (let [field-data (expand-field field-type op-id)]
         (if (some? field-data) (conj fields field-data) fields))))))

(defn gen-data-path [xs] (->> xs butlast (mapcat (fn [x] [x :children]))))

(defn move-behind? [from to]
  (cond
    (or (empty? from) (empty? to)) false
    (= (first from) (first to)) (recur (rest from) (rest to))
    :else (< (first from) (first to))))

(defn move-front? [from to]
  (cond
    (or (empty? from) (empty? to)) false
    (= (first from) (first to)) (recur (rest from) (rest to))
    :else (> (first from) (first to))))

(defn on-same-branch? [from to]
  (and (= (count from) (count to)) (= (butlast from) (butlast to))))

(defn safe-insert-nth [idx y xs]
  (if (or false (> idx (count xs))) (conj (vec xs) y) (insert-nth idx y xs)))

(defn safe-update-in [data path f]
  "do not break when path is empty"
  (if (empty? path) (f data) (update-in data path f)))

(defn drag-field [store data]
  (let [from (:from data)
        to (:to data)
        target (get-in store (concat [:fields] (gen-data-path from) [(last from)]))]
    (update
     store
     :fields
     (fn [fields]
       (cond
         (on-same-branch? from to)
           (safe-update-in
            fields
            (gen-data-path from)
            (fn [xs]
              (->> xs (remove-nth (last from)) (safe-insert-nth (last to) target) (vec))))
         (contains-branch? from to) fields
         (move-front? from to)
           (-> fields
               (safe-update-in
                (gen-data-path from)
                (fn [xs] (->> xs (remove-nth (last from)) (vec))))
               (safe-update-in
                (gen-data-path to)
                (fn [xs] (->> xs (insert-nth (last to) target) (vec)))))
         (move-behind? from to)
           (-> fields
               (safe-update-in
                (gen-data-path to)
                (fn [xs] (->> xs (insert-nth (last to) target) (vec))))
               (safe-update-in
                (gen-data-path from)
                (fn [xs] (->> xs (remove-nth (last from)) (vec)))))
         :else
           (-> fields
               (safe-update-in
                (gen-data-path from)
                (fn [xs] (->> xs (remove-nth (last from)) (vec))))
               (safe-update-in
                (gen-data-path to)
                (fn [xs] (->> xs (insert-nth (last to) target) (vec))))))))))

(defn remove-field [store data]
  (let [data-path (->> data butlast (mapcat (fn [x] [x :children])))]
    (update-in
     store
     (concat [:fields] data-path)
     (fn [fields] (vec (remove-nth (last data) fields))))))

(defn updater [store op data op-id op-time]
  (case op
    :states (update-states store data)
    :create-field (create-field store data op-id op-time)
    :remove-field (remove-field store data)
    :drag-field (drag-field store data)
    :hydrate-storage data
    store))
