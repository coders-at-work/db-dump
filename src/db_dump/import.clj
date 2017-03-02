(ns db-dump.import
  (:require [taoensso.timbre :as log]
            [clojure.java.io :refer (file)]
            [csk.core :refer (read-from)]
            [ladybird.data.core :refer (query add! modify!)]
            [ladybird.data.cond :refer (make)]
            [ladybird.db.core :as db]
            [ladybird.db.transaction :refer (do-tx)]
            [korma.db :as kdb]
            [clojure.java.jdbc :as jdbc]
            )
  )

; TODO extract set identity insert on/off function, how about database other then mssql?
(defn enable-insert-into-identity [db-conn table-name]
  (jdbc/db-do-commands db-conn (format "SET IDENTITY_INSERT %s ON" (name table-name)))
  )

; TODO extract set identity insert on/off function, how about database other then mssql?
(defn disable-insert-into-identity [db-conn table-name]
  (jdbc/db-do-commands db-conn (format "SET IDENTITY_INSERT %s OFF" (name table-name)))
  )

; TODO how do we know which column is rowversion data type
(defn insert-row [table-name {:keys [id] :as row}]
  (log/debug "inserting record of id" id)
  (add! table-name (dissoc row :version))
  )

; TODO how do we know which column is rowversion data type
(defn update-row [table-name {:keys [id] :as row}]
  (log/debug "updating record of id" id)
  (modify! table-name (make (= :id id)) (dissoc row :id :version))
  )

(defn import-table-from-file [dir file-name]
  (let [{:keys [dict rows]:as data} (read-from (file dir file-name))
        {:keys [table-name]} dict
        row-count (count rows)]
    (log/debug (format "importing %d records into table %s" row-count table-name))
    (when (pos? row-count)
      (let [db-conn (db/get-cur-db-conn)]
        (do-tx db-conn
               (enable-insert-into-identity kdb/*current-conn* table-name)
               (doseq [{:keys [id ] :as row} rows]
                 (if-let [db-row (first (query table-name (make (= :id id))))]
                   (update-row table-name row)
                   (insert-row table-name row)
                   )
                 )
               (disable-insert-into-identity kdb/*current-conn* table-name)
               )
        )
      )
    )
  )
