(ns cellular-audiomata.io
  (:require [clojure.string :as str]))

(def life-1-06-header "#Life 1.06")

(defn- load-life-1-06 [s]
  (doall (->> s
           (map #(str/split % #"\s+"))
           (map (fn[[x y]]  [(Integer/parseInt x) (Integer/parseInt y)]))
           (into #{}))))

(defn load-from-file [f]
  (with-open [rdr (clojure.java.io/reader f)]
    (let [s (line-seq rdr)]
      (if (= life-1-06-header (first s))
        (load-life-1-06 (rest s))
        (prn "File Format Not Supported.")))))

;(load-from-file "./resources/glider.lif")
