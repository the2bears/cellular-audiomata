(ns cellular-audiomata.core
  (:require [cellular-audiomata.conway :as conway]
            [cellular-audiomata.display :as display])
  (:gen-class))

(defn -main [& args]
  (display/render 1))
