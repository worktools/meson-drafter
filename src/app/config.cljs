
(ns app.config )

(def cdn?
  (cond
    (exists? js/window) false
    (exists? js/process) (= "true" js/process.env.cdn)
    :else false))

(def dev?
  (let [debug? (do ^boolean js/goog.DEBUG)]
    (if debug?
      (cond
        (exists? js/window) true
        (exists? js/process) (not= "true" js/process.env.release)
        :else true)
      false)))

(def site
  {:port 5021,
   :title "Meson Drafter",
   :icon "http://cdn.tiye.me/logo/cumulo.png",
   :dev-ui "http://localhost:8100/main.css",
   :release-ui "http://cdn.tiye.me/favored-fonts/main.css",
   :cdn-url "http://cdn.tiye.me/meson-drafter/",
   :cdn-folder "tiye.me:cdn/meson-drafter",
   :upload-folder "tiye.me:repo/jimengio/meson-drafter/",
   :server-folder "tiye.me:servers/meson-drafter",
   :theme "#eeeeff",
   :storage-key "meson-drafter",
   :storage-file "storage.edn"})
