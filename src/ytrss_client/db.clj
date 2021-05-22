(ns ytrss-client.db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]
            [clojure.string]))


(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     (clojure.string/join [(System/getProperty "user.home") "/.ytrss-client/db/database.db"])
   })

;; (def db-dev
;;   {:clessname   "org.sqlite.JDBC"
;;    :subprotocol "sqlite"
;;    :subname     "db/database.db"
;;    })

(defn create_db
  "Creates database if not created yet."
  []
  (if (not (.exists (clojure.java.io/file (:subname db))))
    (try (jdbc/db-do-commands db
                            (jdbc/create-table-ddl :entries
                                                   [[:uri :text :unique]
                                                    [:title :text]
                                                    [:author :text]
                                                    [:published :datetime]]))
        (catch Exception e
            (println (.getMessage e))))))


(defn insert_entry
  "insert into entries table"
  [entry]
  (try
    (let [
          uri (first (into '() (:uri entry)))
          title (first (into '() (:title entry)))
          author (first (first (into '() (:author entry))))
          published (first (first (into '() (:published entry))))]
      (jdbc/insert! db :entries {:uri uri :title (first title) :author author :published published}))
    (catch Exception e
         println (.getMessage e))))

(defn load_entries
  []
  (jdbc/query db ["select * from entries"]))

(defn print_entries
  [query]
  (doseq [row query]
    (println row)))
