(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display])
  (:gen-class))

(defn -main [& args]
  (do
    (display/start)
    (loop [life conway/glider
           count 20]
      (display/render life)
      (if (not= 0 count)
        (recur (conway/conway-stepper life) (dec count))
        (display/stop)))))
