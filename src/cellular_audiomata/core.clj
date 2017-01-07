(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io])
  (:gen-class))

(def boundaries {:x-min 0 :x-max 40 :y-min 0 :y-max 20})
(def conway (conway/stepper (merge conway/conway-rules boundaries)))

(defn -main [& args]
  (do
    (display/start)
    (loop [life (io/load-from-file "./resources/glider.lif" [20 10])]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 150)
            (recur (conway life)))
          (display/stop))))))
