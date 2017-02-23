(defproject db-dump "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ladybird "0.5.3-SNAPSHOT"]
                 [clj-time "0.11.0"]
                 ]
  :profiles {:dev {:dependencies [[local.repo/sqljdbc "4.2"]
                                  ]
                   }
             }
  )
