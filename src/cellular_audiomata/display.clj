(ns cellular-audiomata.display
  (:require [clojure.pprint :refer :all]
            [lanterna.screen :as console]))

(def scr (console/get-screen :text))

(defn start []
  (do
    (console/start scr)
    (console/clear scr)
    (console/get-key scr)
    (console/redraw scr)))

(defn stop []
  (console/stop scr))

(defn render [{:keys [alive] :as world}]
  (do
    (console/clear scr)
    (doseq [[x y] alive]
      (console/put-string scr (+ 10 x) (+ 10 y) "*"))
    (let [[_ y] (console/get-size scr)]
      (console/move-cursor scr 0 (dec y)))
    (console/redraw scr)
    (console/get-key scr)))
