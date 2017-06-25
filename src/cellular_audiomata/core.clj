(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io])
  (:gen-class))

(def boundaries {:observe-borders? false :x-min 0 :x-max 20 :y-min 0 :y-max 20})
(def conway (conway/stepper (merge conway/conway-rules boundaries)))

(defn system-exit []
   (do
     (prn :exiting)
     (System/exit 0)))

(defn -main [& args]
  (let [pattern (io/load-from-file "./resources/glider.lif" [10 10])
        life-start {:alive pattern}]
    (display/start-display)
    (loop [life life-start]
      (let [k (display/render life)]
        (if (not= \q k)
          (do
            (Thread/sleep 150)
            (let [next-gen (conway life)]
              ;(display/handle-triggers next-gen)
              (recur next-gen)))
          (do
            (println :exiting)
            (display/stop-display)
            (system-exit)))))))
