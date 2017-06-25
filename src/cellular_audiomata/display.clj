(ns cellular-audiomata.display
  (:require [clojure.pprint :refer :all]
            [lanterna.screen :as console]
            [overtone.live :refer :all :as overtone]
            [overtone.inst.piano :refer :all]))

(def scr (console/get-screen :text))

(defn start-display []
  (do
    (console/start scr)
    (console/clear scr)
    (console/get-key scr)
    (console/redraw scr)))

(defn stop-display []
  (do
    (console/stop scr)
    (overtone/stop)
    (overtone.sc.server/kill-server)))
    

(defn render [{:keys [births deaths survived alive] :as world}]
  (do
    (console/clear scr)
    (doseq [[x y] survived]
      (console/put-string scr x y "*" {:fg :blue}))
    (doseq [[x y] births]
      (console/put-string scr x y "*" {:fg :green})))
      ;(piano (+ x 30))))
  (let [[_ y] (console/get-size scr)]
    (console/move-cursor scr 0 (dec y)))
  (console/redraw scr)
  (console/get-key scr))

(defn handle-triggers [{:keys [births deaths survived alive] :as world}]
  (doseq [[x y] births]
    (piano (+ x 30))))
