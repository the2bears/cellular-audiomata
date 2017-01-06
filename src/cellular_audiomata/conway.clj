;Thanks to Manuel Rotta for this interesting approact to Conway's Life. Code is inspired and copied from his blog post:
;http://programmablelife.blogspot.com/2012/08/conways-game-of-life-in-clojure.html

(ns cellular-audiomata.conway)

(defn neighbours
  "Determines all the neighbours of a given coordinate"
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn stepper
  "Returns a step function for Life-like cell automata.
  neighbours takes a location and return a sequential collection
  of locations. survive? and birth? are predicates on the number
  of living neighbours."
  [{:keys [neighbours birth? survive?] :as config}]
  (fn [cells]
    (let [next-gen (set (for [[loc n] (frequencies (mapcat neighbours cells))
                              :when (if (cells loc) (survive? n) (birth? n))]
                          loc))]
      (->> next-gen
           (filter (fn[[x y]](and (< x 40) (< y 20) (< 0 x) (< 0 y))))
           (set)))))

; patterns
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

; steppers - This is the main function created with the standard rules
(def conway-rules {:neighbours neighbours :birth? #{3} :survive? #{2 3}})
(def conway-stepper (stepper conway-rules)); #{3} #{2 3}))
