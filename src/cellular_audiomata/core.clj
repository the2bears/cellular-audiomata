(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io])
  (:gen-class))

(defn -main [& args]
  (do
    (display/start)
    (loop [life (io/load-from-file "./resources/glidersbythedozen.lif" [60 20])]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 150)
            (recur (conway/conway-stepper life)))
          (display/stop))))))
