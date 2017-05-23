;Thanks to Manuel Rotta for this interesting approach to Conway's Life. Code is inspired and adapted from his blog post:
;http://programmablelife.blogspot.com/2012/08/conways-game-of-life-in-clojure.html

(ns cellular-audiomata.conway
  (:require [clojure.set :as cset]))

(defonce ^:private pattern-registry-ref (atom {}))

(defn pattern-registry []
  @pattern-registry-ref)

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
      (<= x-min x x-max)
      (<= y-min y y-max))))

(defn stepper
  "Returns a step function for Life-like cell automata.
  neighbours takes a location and return a sequential collection
  of locations. survive? and birth? are predicates on the number
  of living neighbours."
  [{:keys [birth? survive? observe-borders? x-min x-max y-min y-max]
    :or {neighbours neighbours x-min 0 x-max 40 y-min 0 y-max 20} :as config}]
  (let [f-b (if observe-borders? (filter-borders config) identity)]
    (fn [{:keys [alive] :as life}]
      (let [next-gen (set (for [[loc n] (frequencies (mapcat neighbours alive))
                                :when (if (alive loc) (survive? n) (birth? n))]
                            loc))
            births (cset/difference next-gen alive)
            deaths (cset/difference alive next-gen)
            survived (cset/difference next-gen births)]
        (if observe-borders?
          {:births births :deaths deaths :survived survived :alive (->> next-gen
                                                                        (filter f-b)
                                                                        (set))}
          {:births births :deaths deaths :survived survived :alive next-gen})))))

(defrecord pattern [name cells])

(defn create-pattern 
  ([{:keys [name cells]}]
   (create-pattern name cells))
  ([name cells]
   (let [pattern (->pattern name cells)]
     (swap! pattern-registry-ref assoc name pattern)
     pattern)))

(comment
  "2nd example of each seems better. Check hiccup and play-cljs"
  (add pattern-name :at x y :rotate 90 :around x2 y2)
  (add (rotate pattern-name 90 :around x2 y2) :at x y)
  (add pattern-name :at x y :flip :vertical :on y2)
  (add (flip pattern-name :vertical :on y2) :at x y)
  (generate-grid [[:pattern {:name pattern-name :x x :y y 
                             :rotate 90 :cx x2 :cy y2}]
                  [:pattern {:load resource :as pattern-name2 :x x :y y
                             :flip-vertical y2}]]))
                  

(defn translate [[x y] dx dy]
  [(+ x dx) (+ y dy)])

(defn rotate [[x y] d cx cy]
  (let [[x y] (translate [x y] (- cx) (- cy))
        p (case d
             90 [y (- x)] 
             180 [(- x) (- y)]
             270 [(- y) x]
             default [x y])]
     (translate p cx cy)))  

(defn rotate-cells [cells d cx cy]
  (let [s (seq cells)]
    (->> (map #(rotate % d cx cy) s)
         (set))))

; patterns
(def blinker #{[2 1] [2 2] [2 3]})
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

; steppers - This is the main function created with the standard rules
(def conway-rules {:birth? #{3} :survive? #{2 3}});:neighbours neighbours
(def conway-stepper (stepper conway-rules)); #{3} #{2 3}))
