(ns db-dump.import
  (:require [clojure.java.io :refer (file)]
            [csk.core :refer (read-from)]
            )
  )

(defn import-table-from-file [dir file-name]
  (let [data (read-from (file dir file-name))]
    data
    ; TODO write data to db
    )
  )
