(ns cellular-audiomata.pattern
  (:require [clojure.set :refer [union] :as set]
            [clojure.pprint :refer [pprint]]))

(defonce ^:private pattern-registry-ref (atom {}))

(defn pattern-registry []
  @pattern-registry-ref)

(defn clear-registry []
  (reset! pattern-registry-ref {}))

(defn- get-pattern [n]
  ((pattern-registry) n #{}))

(defn add
  ([a]
   (let [a (if (instance? String a) (get-pattern a) a)]
     a))
  ([a b]
   (let [a (if (instance? String a) (get-pattern a) a)
         b (if (instance? String b) (get-pattern b) b)]
     (set/union a b)))
  ([a b & more]
   (let [coll (concat [a b] more)]
     (reduce add coll))))

(defn- translate* [[x y] dx dy]
  [(+ x dx) (+ y dy)])

(defn translate [p dx dy]
  (let [p (if (instance? String p) (get-pattern p) p)
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
   (let [p (if (instance? String p) (get-pattern p) p)
         s (seq p)]
     (set (map #(rotate* % d cx cy) s)))))

(defn- flip* [[x y] axis a]
  (let [x? (= axis :x)
        y? (not x?)
        d (if x? (- a x) (- a y))]
    [(if x? (+ a d) x) (if y? (+ a d) y)]))

(defn flip [p axis a]
  (let [p (if (instance? String p) (get-pattern p) p)
        s (seq p)]
    (set (map #(flip* % axis a) s))))
 
(defn store-pattern 
  ([name p]
   (let [pattern (set p)]
     (swap! pattern-registry-ref assoc name pattern)
    pattern)))

(defmulti pattern! (fn [patterns opts]
                     (first patterns)))             

(defmethod pattern! :add [pattern parent-opts & more]
  (let [[command opts & children] pattern
         {:keys [pattern]} opts]
    [command :pattern pattern :more children]))

(defmethod pattern! :flip [pattern parent-opts & more]
  (let [[command opts & children] pattern
         {:keys [pattern axis a]} opts]
    [command :axis axis :a a :more children]))

(defmethod pattern! :rotate [pattern parent-opts & more]
  (let [[command opts & children] pattern
         {:keys [pattern d cx cy], :or {cx 0 cy 0}} opts]
    [command :pattern pattern :d d :cx cx :cy cy :more children]))

(defmethod pattern! :translate [pattern parent-opts & more]
  (let [[command opts & children] pattern
         {:keys [pattern dx dy]} opts]
    [command :pattern pattern :dx dx :dy dy :more children]))

(defmethod pattern! :default [patterns parent-opts & more]
  (cond
   (sequential? (first patterns))
   (map #(pattern! % parent-opts) patterns)
   (nil? (first patterns))
   nil))

(defn create-world [patterns]
  (pattern! patterns {}))

(pprint (create-world [[:add {:pattern "blinker"} :as "blinker2"]
                       [:flip {:pattern "glider" :axis :x :a 5} :as "flipped"]
                       [:rotate {:pattern glider2 :d 180} :as "rotated"]
                       [:translate {:pattern light-spaceship :dx 10 :dy 10}]]))
(comment
  "2nd example of each seems better. Check hiccup and play-cljs"
  (add pattern-name :at x y :rotate 90 :around x2 y2)
  (add (rotate pattern-name 90 :around x2 y2) :at x y)
  (add pattern-name :at x y :flip :vertical :on y2)
  (add (flip pattern-name :vertical :on y2) :at x y)
  (generate-grid [[:pattern {:name pattern-name :x x :y y 
                             :rotate 90 :cx x2 :cy y2}]
                  [:pattern {:load resource :as pattern-name2 :x x :y y
                             :flip-vertical y2}]])
  [:add {:pattern "blinker" :x x :y y} :as "blinker2"]
  [:add {:pattern [:rotate {:pattern glider :d 90 :cx 2 :cy 2}]} :as "pattern1"]
  [:flip {:pattern "glider" :axis :x :a 5} :as "flipped"]
  [:translate {:pattern [:rotate {:pattern blinker :d 180}] :dx 4 :dy -2}])

    
; patterns
(def blinker #{[2 1] [2 2] [2 3]})
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})
