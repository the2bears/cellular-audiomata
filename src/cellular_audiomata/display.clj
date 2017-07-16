(ns cellular-audiomata.display
  (:require [clojure.pprint :refer :all]
            [lanterna.screen :as console]
            [overtone.live :refer :all :as overtone]
            [overtone.inst.piano :refer :all]))

(defn- render* [scr {:keys [births deaths survived alive] :as world}]
  (do
    (console/clear scr)
    (doseq [[x y] survived]
      (console/put-string scr x (- (second (console/get-size scr)) y) "*" {:fg :blue}))
    (doseq [[x y] births]
      (console/put-string scr x (- (second (console/get-size scr)) y) "*" {:fg :green})))
                                        ;(piano (+ x 30))))
  (let [[_ y] (console/get-size scr)]
    (console/move-cursor scr 0 (dec y))
    (console/redraw scr)
    (console/get-key scr)))

(defprotocol Display
  (render! [this world]))

(extend-protocol Display
  com.googlecode.lanterna.screen.Screen
  (render! [this world] (render* this world)))

(defn start-display [scr]
  (do
    (console/start scr)
    (console/clear scr)
    (console/get-key scr)
    (console/redraw scr)))

(defn stop-display [scr]
  (do
    (console/stop scr)
    (overtone/stop)
    (overtone.sc.server/kill-server)))

(defn handle-triggers [{:keys [births deaths survived alive] :as world}]
  (doseq [[x y] births]
    (piano (+ x 30))))
