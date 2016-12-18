;Thanks to Manuel Rotta for this interesting approact to Conway's Life. Code is inspired and copied from his blog post:
;http://programmablelife.blogspot.com/2012/08/conways-game-of-life-in-clojure.html

(ns cellular-audiomata.conway)

(defn create-world
  "Creates rectangular world with the specified width and height.
  Optionally takes coordinates of living cells."
  [w h & living-cells]
  (vec (for [y (range w)]
         (vec (for [x (range w)]
                (if (contains? (first living-cells) [y x]) "X" " "))))))

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
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

; patterns
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

; steppers
(def conway-stepper (stepper neighbours #{3} #{2 3}))

(defn conway
  "Generates world of given size with initial pattern in specified generation"
  [[w h] pattern iterations]
   (->> (iterate conway-stepper pattern)
        (drop iterations)
        first
        (create-world w h)
        (map println)))

(conway-stepper glider2)

(conway [10 10] glider 3)

(create-world 10 10 glider)

(mapcat neighbours glider)
(frequencies (mapcat neighbours glider))

(def c (for [[loc n] (frequencies (mapcat neighbours glider)) :when (if (glider loc) (#{2 3} n) (#{3} n))]
  loc))
c

(if (glider [3 2]) true false)

(if (#{2 3} 4) true false)
