(ns db-dump.util.datetime-utils
  (:require [clj-time.coerce :refer (to-date-time to-sql-time)]
            [clj-time.format :refer (parse)]))

(defn sql-timestamp-to-datetime [t]
  (to-date-time t))

(defn datetime-to-sql-timestamp [t]
  (to-sql-time t))

(defn datetime-to-utc-str [t]
  (str t))

(defn utc-str-to-datetime [s]
  (parse s))

(defn sql-timestamp-to-utc-str [t]
  (-> t
      sql-timestamp-to-datetime
      datetime-to-utc-str)
  )

(defn utc-str-to-sql-timestamp [s]
  (-> s
      utc-str-to-datetime
      datetime-to-sql-timestamp)
  )
