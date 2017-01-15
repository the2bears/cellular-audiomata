(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io])
  (:gen-class))

(def boundaries {:observe-borders true :x-min 0 :x-max 40 :y-min 0 :y-max 20})
(def conway (conway/stepper (merge conway/conway-rules boundaries)))

(defn -main [& args]
  (let [pattern (io/load-from-file "./resources/glider.lif" [20 10])
        life-start {:alive pattern}]
    (display/start)
    (loop [life life-start]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 150)
            (recur (conway life)))
          (display/stop))))))
