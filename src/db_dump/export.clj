(ns db-dump.export
  (:require [ladybird.data.core :refer (query)]
            [clojure.java.io :refer (file)]
            [csk.core :refer (write-to)]
            ))

(defn export-table
  ([table-name]
   (export-table table-name nil))
  ([table-name condition]
   (let [tb-name (name table-name)]
     {:dict {:table-name tb-name}
      :rows (query tb-name condition)
      }
     )
   )
  )

(defn export-table-to-file
  ([dir table-name]
   (export-table-to-file dir table-name nil))
  ([dir table-name condition]
   (export-table-to-file dir (name table-name) table-name condition))
  ([dir file-name table-name condition]
   (let [data (export-table table-name condition)]
     (write-to (file dir file-name) data)
     (assoc data :file-name file-name))))

(defn make-child-condition [parent-recs {:keys [fk ref] :as child-def}]
  (->> parent-recs (map #(ref %)) (list 'in fk)))

(defn export-child
  "
   child-def should be:
     {:table  :benefit_detail
      :parent :benefit
      :fk     :benefit_id
      :ref    :id
      }
  "
  [export-fn parent-recs {:keys [table] :as child-def}]
  (let [condition (make-child-condition parent-recs child-def)]
    (export-fn table condition)))

(defn export-child-to-file
  [dir parent-recs child-def]
  (export-child (partial export-table-to-file dir) parent-recs child-def))

(defn export-parent
  [export-fn table condition]
  (export-fn table condition))

(defn export-parent-to-file
  [dir table condition]
  (export-parent (partial export-table-to-file dir) table condition))

(defn export-table-tree-to-file
  "
  table-rel-def should be:
  [
  {:table :benefit
  :parent nil
  }
  {:table :benefit_detail
  :parent :benefit
  :fk :benefit_id
  :ref :id
  }
  ]
  Parent table must be prior to child table.
  "
  [dir [parent-table-def & children-table-def :as table-rel-def] condition]
  (let [{:keys [table]} parent-table-def
        data (export-parent-to-file dir table condition)
        ]
    (loop [parent-data-m {table data}
           children-table-def children-table-def
           ]
      (if-let [{:keys [table parent] :as table-def} (first children-table-def)]
        (let [rows (:rows (parent-data-m parent))
              m (export-child-to-file dir rows table-def)
              m (assoc parent-data-m table m)
              ]
          (recur m (rest children-table-def)))
        (mapv (fn [[table {:keys [rows file-name]}]] {:table table :row-count (count rows) :file-name file-name}) parent-data-m)))))

