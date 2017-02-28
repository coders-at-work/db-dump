(ns db-dump.import
  (:require [taoensso.timbre :as log]
            [clojure.java.io :refer (file)]
            [csk.core :refer (read-from)]
            [ladybird.data.core :refer (query add! modify!)]
            [ladybird.data.cond :refer (make)]
            [ladybird.db.core :as db]
            [ladybird.db.transaction :refer (do-tx)]
            [korma.core :refer (exec-raw)]
            [korma.db :as kdb]
            [clojure.java.jdbc :as jdbc]
            )
  )

; TODO extract set identity insert on/off function, how about database other then mssql?
; TODO refacto exec-raw to ladybird?
(defn enable-insert-into-identity [db-conn table-name]
  ; (exec-raw db-conn (format "SET IDENTITY_INSERT %s ON" (name table-name)))
  ; (let [conn kdb/*current-conn*
  ;       st (.createStatement conn)]
    ; (println :on-conn conn)
    ; (.execute st (format "SET IDENTITY_INSERT %s ON" (name table-name)))

    ; )
    (jdbc/db-do-commands db-conn (format "SET IDENTITY_INSERT %s ON" (name table-name)))
  (println :ON)
  )

; TODO extract set identity insert on/off function, how about database other then mssql?
(defn disable-insert-into-identity [db-conn table-name]
  ; (exec-raw db-conn (format "SET IDENTITY_INSERT %s OFF" (name table-name)))
  ; (let [conn kdb/*current-conn*
  ;       st (.createStatement conn)]
  ;   (println :off-conn conn)
  ;   (.execute st (format "SET IDENTITY_INSERT %s OFF" (name table-name)))
  ;   )

    (jdbc/db-do-commands db-conn (format "SET IDENTITY_INSERT %s ON" (name table-name)))
  (println :OFF)
  )

; TODO how to detect version row
(defn insert-row [table-name {:keys [id] :as row}]
  (log/debug "inserting record of id" id)
  (add! table-name (dissoc row :version))
  )

; TODO how to detect version row
(defn update-row [table-name {:keys [id] :as row}]
  (log/debug "updating record of id" id)
  (modify! table-name (make (= :id id)) (dissoc row :id :version))
  )

(defn import-table-from-file [dir file-name]
  (let [{:keys [dict rows]:as data} (read-from (file dir file-name))
        {:keys [table-name]} dict
        row-count (count rows)]
    ; TODO write data to db
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
               ; (exec-raw (db/get-cur-db-conn) "insert into cms_control (id, user_id, name, value ,create_time, last_update) values (10, 1, 'E', 'E', current_timestamp, current_timestamp);" :results)
               (disable-insert-into-identity kdb/*current-conn* table-name)
               )
        )
      )
    )
  )
