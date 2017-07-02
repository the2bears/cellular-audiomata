(ns cellular-audiomata.pattern
  (:require [clojure.set :refer [union] :as set]
            [clojure.pprint :refer [pprint]]))

(declare pattern!)

(defonce ^:private pattern-registry-ref (atom {}))

(defn pattern-registry []
  @pattern-registry-ref)

(defn clear-registry []
  (reset! pattern-registry-ref {}))

(defprotocol Pattern
  (resolve-pattern [n]))

(extend-protocol Pattern
  String
  (resolve-pattern [n] ((pattern-registry) n #{}))
  clojure.lang.PersistentHashSet
  (resolve-pattern [n] n)
  clojure.lang.PersistentVector
  (resolve-pattern [n] (pattern! n #{}))) 

(defn add
  "Combines n patterns together, returning the set union of all patterns passed in. If only one pattern is passed, just that pattern is returned."
  ([a]
   (let [a (resolve-pattern a)]
     a))
  ([a b]
   (let [a (resolve-pattern a)
         b (resolve-pattern b)]
     (set/union a b)))
  ([a b & more]
   (let [coll (concat [a b] more)]
     (reduce add coll))))

(defn- translate* [[x y] dx dy]
  [(+ x dx) (+ y dy)])

(defn translate 
  "Accepts a pattern p, and returns a pattern where all points within that pattern have been translated by dx and dy"
  [p dx dy]
  (let [p (resolve-pattern p)
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
  "Accepts a pattern p, and rotates it by degrees d around the point given by cx and cy. If not point is specified, the pattern is rotated around 0,0. Rotations will only occur for 0, 90, 180, and 270 degrees."
  ([p d]
   (rotate p d 0 0)) 
  ([p d cx cy]
   (let [p (resolve-pattern p)
         s (seq p)]
     (set (map #(rotate* % d cx cy) s)))))

(defn- flip* [[x y] axis a]
  (let [x? (= axis :x)
        y? (not x?)
        d (if x? (- a x) (- a y))]
    [(if x? (+ a d) x) (if y? (+ a d) y)]))

(defn flip
  "Accepts a pattern p, and flips it on the axis (:x or :y) along the line designated by the value a."
  [p axis a]
  (let [p (resolve-pattern p)
        s (seq p)]
    (set (map #(flip* % axis a) s))))
 
(defn store-pattern
  "Stores the pattern p in the registry, mapped to the given name." 
  ([p name]
   (let [pattern (set p)]
     (swap! pattern-registry-ref assoc name pattern)
    pattern)))

(defn- maybe-store-pattern [p children]
  (if (and children (= :as (first children)))
      (store-pattern p (second children))
      p))

(defmulti pattern! (fn [patterns opts]
                     (first patterns)))             

(defmethod pattern! :add [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern]} opts
        p (resolve-pattern pattern)]
    (maybe-store-pattern (add p) children)))

(defmethod pattern! :flip [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern axis a]} opts
        p (resolve-pattern pattern)]
    (maybe-store-pattern (flip p axis a) children)))

(defmethod pattern! :rotate [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern d cx cy], :or {cx 0 cy 0}} opts
        p (resolve-pattern pattern)]
    (maybe-store-pattern (rotate p d cx cy) children)))

(defmethod pattern! :translate [pattern parent-opts]
  (let [[command opts & children] pattern
        {:keys [pattern dx dy]} opts
        p (resolve-pattern pattern)]
    (maybe-store-pattern (translate p dx dy) children)))

(defmethod pattern! :default [patterns parent-opts]
  (cond
   (sequential? (first patterns))
   (reduce add (map #(pattern! % parent-opts) patterns))
   (nil? (first patterns))
   nil))

(defn create-world 
  "Accepts a vector of pattern vectors defined by the DSL, combining the patterns into a single set of points."
  [patterns]
  (pattern! patterns {}))
