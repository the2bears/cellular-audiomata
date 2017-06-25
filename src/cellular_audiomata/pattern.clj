(ns cellular-audiomata.pattern)

(defonce ^:private pattern-registry-ref (atom {}))

(defn pattern-registry []
  @pattern-registry-ref)

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

(defn translate-cells [cells dx dy]
  (let [s (seq cells)]
    (->> (map #(translate % dx dy) s)
         (set))))  

(defn rotate
  ([[x y] d]
   (rotate [x y] d 0 0))
  ([[x y] d cx cy]
   (let [[x y] (translate [x y] (- cx) (- cy))
         p (case d
              90 [y (- x)] 
              180 [(- x) (- y)]
              270 [(- y) x]
              default [x y])]
      (translate p cx cy))))  

(defn rotate-cells
  ([cells d]
   (rotate-cells cells d 0 0)) 
  ([cells d cx cy]
   (let [s (seq cells)]
     (->> (map #(rotate % d cx cy) s)
          (set)))))

; patterns
(def blinker #{[2 1] [2 2] [2 3]})
(def glider #{[2 0] [2 1] [2 2] [1 2] [0 1]})
(def glider2 #{[3 0] [3 1] [3 2] [2 2] [1 1]})
(def light-spaceship #{[2 0] [4 0] [1 1] [1 2] [1 3] [4 3] [1 4] [2 4] [3 4]})
