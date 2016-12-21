(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display])
  (:gen-class))

(defn -main [& args]
  (do
    (display/start)
    (loop [life conway/glider]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 300)
            (recur (conway/conway-stepper life)))
          (display/stop))))))
