(ns ytrss-client.getfeed)

(require '[clojure.xml])

(declare Entry)
(defstruct Entry :uri :title :author :published)

(defn getTitle
  [entry]
  (for [item entry
        :when (= :title (:tag item))]
    (:content item)))

(defn getAuthor
  [entry]
  (for [item entry
        :when (= :author (:tag item))]
    (:content (first (:content item)))))

(defn getUri
  [entry]
  (for [item entry
        :when (= :link (:tag item))]
    (:href (:attrs item))))

(defn getPublished
  [entry]
  (for [item entry
        :when (= :published (:tag item))]
    (:content item)))

(defn entryParams
  "Get video params"
  [entry]
  (struct-map Entry
              :uri (getUri entry)
              :title (getTitle entry)
              :author (getAuthor entry)
              :published (getPublished entry)))

(defn getEntries
  "Get all entries in xml."
  [parsed-xml]
  (for [item (xml-seq parsed-xml)
        :when (= :entry (:tag item))]
    (:content item)))

(defn getFeedFromAddress
  "Get videos from address."
  [addr]
  (try (let [feed (slurp addr)]
    (map entryParams (getEntries (clojure.xml/parse (java.io.ByteArrayInputStream. (.getBytes feed)))))
    )
       (catch Exception e
         (println "Couldn't connect to address: " addr))))
