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

(defn translate [p dx dy]
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

(defn flip [p axis a]
  (let [p (resolve-pattern p)
        s (seq p)]
    (set (map #(flip* % axis a) s))))
 
(defn store-pattern 
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

(defn create-world [patterns]
  (pattern! patterns {}))
