(defproject ytrss-client "0.1.0-SNAPSHOT"
  :description "Client for viewing rss from youtube channels."
  :url "https://github.com/soukev/ytrss-client"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.xerial/sqlite-jdbc "3.34.0"]
                 [seesaw "1.4.5"]]
  :main ^:skip-aot ytrss-client.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
