(ns cellular-audiomata.io
  (:require [clojure.string :as str]))

(def life-1-06-header "#Life 1.06")

(defn- load-life-1-06 [s]
  (doall (->> s
           (map #(str/split % #"\s+"))
           (map (fn[[x y]]  [(Integer/parseInt x) (Integer/parseInt y)])))))

(defn load-from-file
  ([f]
   (load-from-file f [0 0]))
  ([f [off-x off-y]]
   (with-open [rdr (clojure.java.io/reader f)]
     (let [s (line-seq rdr)]
       (cond (= life-1-06-header (first s))
             (->>(load-life-1-06 (rest s))
                 (map (fn[[x y]] [(+ x off-x)(+ y off-y)]))
                 (into #{}))
             :else
             (prn "File Format Not Supported."))))))

;(load-from-file "./resources/glider.lif" [10 10])
