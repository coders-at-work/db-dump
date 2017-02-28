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
   (write-to (file dir (name table-name)) (export-table table-name condition))
   )
  )

(defn export-table-tree-to-file
  [dir root-table condition table-rel-def])

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
