(ns ytrss-client.core
  (:gen-class)
  (:require [clojure.java.io :as io]))

;; other modules
(require 'ytrss-client.getfeed)
(require 'ytrss-client.db)

;; other clojure libs
(require 'clojure.string)
(require 'clojure.java.shell)
(require 'clojure.edn)

;; seesaw libs
(require '[seesaw.core :as saw])
(require '[seesaw.keymap :as sawkey])
(require '[seesaw.table :as sawtable])

;; Config files and bd paths
(def subs-f (clojure.string/join [(System/getProperty "user.home") "/.ytrss-client/subs.conf"]))
(def conf (clojure.string/join [(System/getProperty "user.home") "/.ytrss-client/config.conf"]))
(def db-f (clojure.string/join [(System/getProperty "user.home") "/.ytrss-client/db/database.db"]))

(defn is-background
  "Is string background config?"
  [x]
  (= "background" (first (clojure.string/split x #"="))))

(defn get-background
  "Get backgroun color."
  []
  (try
    (let [lines (clojure.string/split-lines (slurp (io/file conf)))]
      (second (clojure.string/split (first (filter is-background lines)) #"=")))
    (catch Exception e
      "#2c3e50")))


(defn is-foreground
  "Is string foreground config?"
  [x]
  (= "foreground" (first (clojure.string/split x #"="))))

(defn get-foreground
  "Get foreground color."
  []
  (try
    (let [lines (clojure.string/split-lines (slurp (io/file conf)))]
      (second (clojure.string/split (first (filter is-foreground lines)) #"=")))
    (catch Exception e
      "#f2f3f4")))

(def table
  "Table model."
  (saw/table :model [:columns [{:key :title :text "Title"}
                                   ;; {:key :uri :text "Url"}
                                   {:key :author :text "Channel"}
                                   {:key :published :text "Published"}]
                     ]
             :selection-mode :single
             :background (get-background)
             :foreground (get-foreground)
             :font "ARIAL-12"))

;; make table scrollable
(def scrolltable (saw/scrollable table))

(defn get_lines
  "Get lines from file"
  [file]
  (try
    (clojure.string/split-lines (slurp (io/file file)))
    (catch Exception e
      (seq []))))

(defn get_all_entries
  "Get all entries from subs, separate comments from urls"
  [subs]
  (mapcat #(ytrss-client.getfeed/getFeedFromAddress (first (clojure.string/split %1 #" +#"))) subs))

(defn updateTable
  "Fetch all from database and put it to table sorted by datetime."
  [t]
  (let [res (reverse (sort-by :published (ytrss-client.db/load_entries)))]
    (doseq [[i r] (map-indexed vector res)]
      (sawtable/insert-at! t i r))))


(defn updateSubs
  "Fetch all subscription rss feeds and update database."
  []
  (let [entries (get_all_entries (get_lines subs-f))]
    (doseq [i entries] ( ytrss-client.db/insert_entry i))))

(defn check-conf-dir
  "check configs, if there are no config files create them"
  []
  (if (not (.exists (io/file subs-f)))
    (do (io/make-parents subs-f)
        (spit subs-f "")))
  (if (not (.exists (io/file conf)))
    (do (io/make-parents conf)
        (spit conf "background=#2c3e50\nforeground=#f2f3f4")))
  (if (not (.exists (io/file db-f)))
    (io/make-parents db-f)))

(defn -main
  "Checks config files, database and opens GUI"
  [& args]
  (check-conf-dir) ;; check configs
  (ytrss-client.db/create_db) ;; create database if not created yet
  ;; GUI
  (let [f (-> (saw/frame :title "ytrss-client"
               :on-close :exit
               :content scrolltable)
              (saw/pack!)
              (saw/show!))]
    (updateTable table) ;; load table data
    (sawkey/map-key f "S"
                    (fn [e] (if-let [s (saw/selection table)]
                              ;; (println "Selected url: " (:uri (sawtable/value-at table s)))
                              (let [url (:uri (sawtable/value-at table s))]
                                (clojure.java.shell/sh "mpv" url))
                              (saw/alert "Select video first."))))
    (sawkey/map-key f "U"
                    (fn [e]
                      (sawtable/clear! table)
                      (updateSubs)
                      (updateTable table)))))
