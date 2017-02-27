(defproject db-dump "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/timbre "4.1.4"]
                 [clj-time "0.11.0"]
                 [me.raynes/fs "1.4.6"]
                 [coders-at-work/ladybird "0.5.3-SNAPSHOT"]
                 [coders-at-work/csk "0.1.0-SNAPSHOT"]
                 ]
  :profiles {:dev {:dependencies [[local.repo/sqljdbc "4.2"]
                                  ]
                   }
             }
  )
