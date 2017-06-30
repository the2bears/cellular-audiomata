(ns cellular-audiomata.pattern
  (:require [clojure.set :refer [union] :as set]
            [clojure.pprint :refer [pprint]]))

(declare pattern!)

(defonce ^:private pattern-registry-ref (atom {}))

(defn pattern-registry []
  @pattern-registry-ref)

(defn clear-registry []
  (reset! pattern-registry-ref {}))

(defn- get-pattern [n]
  (cond (string? n) ((pattern-registry) n #{})
        (set? n) n
        (sequential? n) (pattern! n #{})))

(defn add
  ([a]
   (let [a (get-pattern a)]
     a))
  ([a b]
   (let [a (get-pattern a)
         b (get-pattern b)]
     (set/union a b)))
  ([a b & more]
   (let [coll (concat [a b] more)]
     (reduce add coll))))

(defn- translate* [[x y] dx dy]
  [(+ x dx) (+ y dy)])

(defn translate [p dx dy]
  (let [p (get-pattern p)
        s (seq p)]
    (set (map #(translate* % dx dy) s))))

(defn- rotate*
  [[x y] d cx cy]
  (let [[x y] (translate* [x y] (- cx) (- cy))
        p (case d
             90 [y (- x)] 
             180 [(- x) (- y)]
             270 [(- y) x]
             [x y])]
    (translate* p cx cy)))  

(defn rotate
  ([p d]
   (rotate p d 0 0)) 
  ([p d cx cy]
   (let [p (get-pattern p)
         s (seq p)]
     (set (map #(rotate* % d cx cy) s)))))

(defn- flip* [[x y] axis a]
  (let [x? (= axis :x)
        y? (not x?)
        d (if x? (- a x) (- a y))]
    [(if x? (+ a d) x) (if y? (+ a d) y)]))

(defn flip [p axis a]
  (let [p (get-pattern p)
        s (seq p)]
    (set (map #(flip* % axis a) s))))
 
(defn store-pattern 
  ([p name]
   (let [pattern (set p)]
     (swap! pattern-registry-ref assoc name pattern)
    pattern)))

(defmulti pattern! (fn [patterns opts]
                     (first patterns)))             

(defmethod pattern! :add [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern]} opts
        p (get-pattern pattern)]
    (add p)))

(defmethod pattern! :flip [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern axis a]} opts
        p (get-pattern pattern)]
    (if children (prn :more children) (prn :no-more))
    (flip p axis a)))

(defmethod pattern! :rotate [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern d cx cy], :or {cx 0 cy 0}} opts
        p (get-pattern pattern)]
    (rotate p d cx cy)))

(defmethod pattern! :translate [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern dx dy]} opts
        p (get-pattern pattern)]
    (translate p dx dy)))

(defmethod pattern! :default [patterns parent-opts]
  (cond
   (sequential? (first patterns))
   (reduce add (map #(pattern! % parent-opts) patterns))
   (nil? (first patterns))
   nil))

(defn create-world [patterns]
  (pattern! patterns {}))

; patterns
(def blinker #{[2 1] [2 2] [2 3]})
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})

(pprint (create-world [[:add {:pattern [:translate {:pattern blinker :dx 2 :dy 0}]}]
                       [:flip {:pattern glider :axis :x :a 5} :as "flipped"]
                       [:rotate {:pattern glider2 :d 90} :as "rotated"]
                       [:translate {:pattern light-spaceship :dx 10 :dy 10}]]))
(comment
  [:add {:pattern "blinker" :x x :y y} :as "blinker2"]
  [:add {:pattern [:rotate {:pattern glider :d 90 :cx 2 :cy 2}]} :as "pattern1"]
  [:flip {:pattern "glider" :axis :x :a 5} :as "flipped"]
  [:translate {:pattern [:rotate {:pattern blinker :d 180}] :dx 4 :dy -2}])

