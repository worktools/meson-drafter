
(ns app.generate
  (:require [clojure.string :as string])
  (:require-macros [clojure.core.strint :refer [<<]]))

(declare gen-nested)

(declare gen-group)

(declare gen-form-items)

(defn format-typescript [text]
  (try
   (js/prettier.format text (clj->js {:parser :typescript, :plugins js/prettierPlugins}))
   (catch js/Error error (str error "\n" "\n" text))))

(defn gen-item [k v] (str k ": " v))

(defn gen-custom [field]
  (let [items (->> [(gen-item "type" (pr-str "custom"))
                    (gen-item "name" (pr-str (:name field)))
                    (gen-item "label" (pr-str (:label field)))
                    (gen-item "required" (str (:required? field)))
                    (gen-item "validator" "(x)=>null")
                    (gen-item "render" "(x)=>\"TODO CUSTOM\"")]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-decorative [field]
  "{\ntype: \"decorative\",\nrender: () => \"TODO DECORATION\"\n}")

(defn gen-input [field]
  (let [items (->> [(gen-item "type" (pr-str "input"))
                    (gen-item "name" (pr-str (:name field)))
                    (gen-item "label" (pr-str (:label field)))
                    (gen-item "required" (str (:required? field)))
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))
                    (gen-item "validator" "(x)=>null")]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-number [field]
  (let [items (->> [(gen-item "type" (pr-str "number"))
                    (gen-item "name" (pr-str (:name field)))
                    (gen-item "label" (pr-str (:label field)))
                    (gen-item "required" (str (:required? field)))
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))
                    (gen-item "validator" "(x)=>null")]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-select [field]
  (let [items (->> [(gen-item "type" (pr-str "select"))
                    (gen-item "name" (pr-str (:name field)))
                    (gen-item "label" (pr-str (:label field)))
                    (gen-item "required" (str (:required? field)))
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))
                    (gen-item "options" (js/JSON.stringify (clj->js (:options field))))
                    (gen-item "validator" "(x)=>null")]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-textarea [field]
  (let [items (->> [(gen-item "type" (pr-str "textarea"))
                    (gen-item "name" (pr-str (:name field)))
                    (gen-item "label" (pr-str (:label field)))
                    (gen-item "required" (str (:required? field)))
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-nested [field]
  (let [items (->> [(gen-item "type" (pr-str "nested"))
                    (gen-item "label" (pr-str (or (:label field) "TODO")))
                    (gen-item "children" (gen-form-items (:children field) {}))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-group [field]
  (let [items (->> [(gen-item "type" (pr-str "group"))
                    (gen-item "label" (pr-str (or (:label field) "TODO")))
                    (gen-item "horizontal" (pr-str true))
                    (gen-item "itemWidth" (pr-str "50%"))
                    (gen-item "children" (gen-form-items (:children field) {}))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-form-items [fields]
  (let [code (->> fields
                  (map
                   (fn [field]
                     (case (:type field)
                       :input (gen-input field)
                       :textarea (gen-textarea field)
                       :select (gen-select field)
                       :decorative (gen-decorative field)
                       :custom (gen-custom field)
                       :number (gen-number field)
                       :group (gen-group field)
                       :nested (gen-nested field)
                       (do (println "Unknown:" field) (<< "{type:\"UNKNOWN: ~{field}}\"}")))))
                  (string/join (str "," "\n")))]
    (str "[" code "]")))

(defn generate-form-code [fields options]
  (let [fields-code (gen-form-items fields)] (format-typescript fields-code)))
