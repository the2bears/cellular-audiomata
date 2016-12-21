(ns cellular-audiomata.io
  (:require [clojure.string :as str]))

(def life-1-06-header "#Life 1.06")

(defn load-from-file [f]
  (with-open [rdr (clojure.java.io/reader f)]
    (let [s (line-seq rdr)]
      (if (= life-1-06-header (first s))
        (doall (->>(rest s)
                   (map #(str/split % #"\s+"))
                   (map (fn[[x y]]  [(Integer/parseInt x) (Integer/parseInt y)]))
                   (into #{})))
        (prn "File Format Not .")))))


;(load-from-file "./resources/glider.lif")
