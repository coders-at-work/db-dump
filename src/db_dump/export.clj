(ns db-dump.export
  (:require [ladybird.data.core :refer (query)]))

(defn export-table
  ([table-name]
   (export-table table-name nil))
  ([table-name condition]
   (let [tb-name (name table-name)]
     {:dict {:table-name tb-name}
      :data (query tb-name condition)
      }
     )
   )
  )

#_(defn export-data [data tagger]
    (tagger data)
    )
