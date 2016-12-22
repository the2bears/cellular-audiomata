(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io])
  (:gen-class))

(defn -main [& args]
  (do
    (display/start)
    (loop [life (io/load-from-file "./resources/glider.lif" [30 10])]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 300)
            (recur (conway/conway-stepper life)))
          (display/stop))))))
