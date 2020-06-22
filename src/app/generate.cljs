
(ns app.generate (:require [clojure.string :as string]))

(defn format-typescript [text]
  (try
   (js/prettier.format text (clj->js {:parser :typescript, :plugins js/prettierPlugins}))
   (catch js/Error error (str error "\n" "\n" text))))

(defn gen-decorative [field]
  "{\ntype: \"decorative\",\nrender: () => <div>TODO DECORATOR</div>\n}")

(defn gen-item [k v] (str k ": " v))

(defn gen-input [field]
  (let [items (->> [(gen-item "type" (pr-str "input"))
                    (gen-item "name" (pr-str "TODO"))
                    (gen-item "label" (pr-str "TODO"))
                    (gen-item "required" "true")
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-select [field]
  (let [items (->> [(gen-item "type" (pr-str "select"))
                    (gen-item "name" (pr-str "TODO"))
                    (gen-item "label" (pr-str "TODO"))
                    (gen-item "required" "true")
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))
                    (gen-item "options" (js/JSON.stringify (clj->js (:options field))))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn gen-textarea [field]
  (let [items (->> [(gen-item "type" (pr-str "textarea"))
                    (gen-item "name" (pr-str "TODO"))
                    (gen-item "label" (pr-str "TODO"))
                    (gen-item "required" "true")
                    (gen-item "inputProps" "{}")
                    (gen-item "placeholder" (pr-str "TODO"))]
                   (string/join (str "," "\n")))]
    (str "{" items "}")))

(defn generate-form-code [fields options]
  (let [fields-code (->> fields
                         (map
                          (fn [field]
                            (case (:type field)
                              :input (gen-input field)
                              :textarea (gen-textarea field)
                              :select (gen-select field)
                              :decorative (gen-decorative field)
                              (do (println "Unknown:" field) "{type:\"\"}"))))
                         (string/join (str "," "\n")))]
    (format-typescript (str "[" fields-code "]"))))
