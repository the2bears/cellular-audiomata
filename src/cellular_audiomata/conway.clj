;Thanks to Manuel Rotta for this interesting approach to Conway's Life. Code is inspired and adapted from his blog post:
;http://programmablelife.blogspot.com/2012/08/conways-game-of-life-in-clojure.html

(ns cellular-audiomata.conway)

(defn- neighbours
  "Determines all the neighbours of a given coordinate"
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn- filter-borders
  "Returns a function predicate that indicates true if the point is within the borders provided"
  [{:keys [x-min x-max y-min y-max]
    :or {x-min 0 x-max 40 y-min 0 y-max 20} :as config}]
  (fn[[x y]]
    (and
      (< x x-max)
      (< y y-max)
      (< x-min x)
      (< y-min y))))

(defn stepper
  "Returns a step function for Life-like cell automata.
  neighbours takes a location and return a sequential collection
  of locations. survive? and birth? are predicates on the number
  of living neighbours."
  [{:keys [birth? survive? observe-borders x-min x-max y-min y-max]
    :or {neighbours neighbours x-min 0 x-max 40 y-min 0 y-max 20} :as config}]
  (fn [cells]
    (let [next-gen (set (for [[loc n] (frequencies (mapcat neighbours cells))
                              :when (if (cells loc) (survive? n) (birth? n))]
                          loc))]
      (if observe-borders
        (->> next-gen
             (filter (filter-borders config))
             (set))
        next-gen))))

; patterns
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

; steppers - This is the main function created with the standard rules
(def conway-rules {:birth? #{3} :survive? #{2 3}});:neighbours neighbours
(def conway-stepper (stepper conway-rules)); #{3} #{2 3}))
