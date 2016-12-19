(ns cellular-audiomata.display
  (:require [clojure.pprint :refer :all]
            [lanterna.screen :as console]))

(def scr (console/get-screen :text))

(defn start []
  (do
    (console/start scr)
    (console/clear scr)))

(defn stop []
  (console/stop scr))

(defn render [world]
    (do
      (console/clear scr)
      (doseq [[x y] world]
        (console/put-string scr (+ 10 x) (+ 10 y) "*"))
      (console/redraw scr)
      (console/get-key-blocking scr)
      ))
