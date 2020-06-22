
(ns app.updater (:require [respo.cursor :refer [update-states]]))

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

(defn updater [store op data op-id op-time]
  (case op
    :states (update-states store data)
    :create-field (create-field store data)
    :hydrate-storage data
    store))
