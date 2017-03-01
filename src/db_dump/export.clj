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
   (let [data (export-table table-name condition)]
     (write-to (file dir (name table-name)) data)
     data)))

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
    (loop [parent-data-m {table (:rows data)}
           children-table-def children-table-def
           ]
          (when-let [{:keys [parent] :as table-def} (first children-table-def)]
                    (let [m (->> (export-child-to-file dir (parent-data-m table) table-def)
                                 :rows
                                 (assoc parent-data-m parent))]
                      (recur m  (rest children-table-def)))))))

#_(defn export-data [data tagger]
    (tagger data)
    )

; (gen-export-fn spec)
; (export-fn dir :benefit condition {:benefit {:table-name "benefit"
;                                              :children {:benefit-detail-1 {:fk :benefit-id :ref :id}
;                                                         :benefit-card-type {:fk :benefit-id}}
;                                    :benefit-detal-1 {:table-name "benefit_detail"}
;                                    :benefit-card-type nil }})

;                                              [{:table :benefit
;                                               :parent nil
;                                               }
;                                               {:table :benefit-detail
;                                                :parent :benefit
;                                                :fk :benefit-id
;                                                :
;                                                }
;                                               ]


; (defn export-table-tree [parent parent-condition [{:table-name :a :fk-name :p-id :ref :id} [b c]]]

; (def rel-definition ^{:root :benefit} {:benefit [{:talbe :benefit-detail :fk :benefit-id}
;                                {:table :benefit-card-type :fk :benefit-id}]
;                      :benefit-card-type [{:table :benefit-card-type-detail :fk :benefit-card-type_id}]
;                      :promotion 
;                      })

; (def benefit-rel-def
; {:benefit [{:talbe :benefit-detail :fk :benefit-id} {:table :benefit-card-type :fk :benefit-id}]
;                      :benefit-card-type [{:table :benefit-card-type-detail :fk :benefit-card-type_id}]
;                      }
;   )
; (def promo-rel-def
; {:promo [{:talbe :benefit-detail :fk :benefit-id} {:table :benefit-card-type :fk :benefit-id}]
;                      :benefit-card-type [{:table :benefit-card-type-detail :fk :benefit-card-type_id}]
;                      }

;   )

; (defmacro action-export [ACTION-TYPE def]
;   `(defn export-benefit))

; (export-benefit '(= :id 1))


; (when has-benefit-action-type
;   (export-table-tree :benefit '(= :id 1) benefit-rel-definition)
; )

; (when has-promo-action-type
; (export-table-tree '(= :id 1) promo-rel-definition)
; )
