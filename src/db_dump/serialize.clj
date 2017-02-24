(ns db-dump.serialize
  (:require [clojure.java.io :refer (output-stream input-stream as-file)]
            [carbonite.api :refer (default-registry)]
            [carbonite.buffer :refer (write-bytes read-bytes)]
            ))

(def ^:private registry (default-registry))

; serialize data to byte array
(defn write-data [data]
  (write-bytes registry data)
  )

; deserialize data from byte array
(defn read-data [byte-arr]
  (read-bytes registry byte-arr)
  )

; write byte array to file
(defn write-file [file byte-arr]
  (with-open [os (output-stream file)]
    (.write os byte-arr)
    )
  )

; read file and return a byte array
(defn read-file [file]
  (let [f (as-file file)
        byte-arr (byte-array (.length f))]
    (with-open [is (input-stream f)]
      (.read is byte-arr)
      byte-arr
      )
    )
  )

; serialize data to file
(defn write-data-to-file [data file]
  (->> data write-data (write-file file))
  )

; deserialize data to file
(defn read-data-from-file [file]
  (->> file read-file read-data)
  )
