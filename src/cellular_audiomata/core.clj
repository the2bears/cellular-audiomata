(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display]
            [cellular-audiomata.io :as io]
            [lanterna.screen :as console])
  (:gen-class))

(def boundaries {:observe-borders? false :x-min 0 :x-max 20 :y-min 0 :y-max 20})
(def conway (conway/stepper (merge conway/conway-rules boundaries)))

(defn system-exit []
   (do
     (prn :exiting)
     (System/exit 0)))

(defn -main [& args]
  (let [pattern (io/load-from-file "./resources/glider.lif"); [10 10])
        screen (console/get-screen :text)
        life-start {:alive pattern}]
    (display/start-display screen)
    (loop [life life-start]
      (let [k (display/render! screen life)]
        (if (not= \q k)
          (do
            (Thread/sleep 150)
            (let [next-gen (conway life)]
              ;(display/handle-triggers next-gen)
              (recur next-gen)))
          (do
            (println :exiting)
            (display/stop-display screen)
            (system-exit)))))))
