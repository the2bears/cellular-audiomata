(ns cellular-audiomata.display
  (:require [lanterna.screen :as console]))

(def scr (console/get-screen :text))

(defn render [world]
    (do (console/start scr)
      (console/put-string scr 10 10 "Hello,Conway!")
      (console/put-string scr 10 11 "Press any key to exit!")
      (console/redraw scr)
      (console/get-key-blocking scr)
    (console/stop scr)))
