(ns cellular-audiomata.pattern-test
  (:require [clojure.set :as set]
            [clojure.test :refer :all]
            [cellular-audiomata.pattern :refer :all]))
  
(defn pattern-test-fixture [f]
  (clear-registry)
  (store-pattern blinker "blinker")
  (store-pattern glider "glider")
  (f))

(use-fixtures :each pattern-test-fixture)

(deftest test-store-pattern []
  (is (= (store-pattern blinker "blinker")
         blinker)))

(deftest test-add []
  (is (= (add blinker)
         blinker))
  (is (= (add "blinker")
         blinker))
  (is (= (add blinker glider) 
         (set/union blinker glider)))
  (is (= (add "blinker" glider)
         (set/union blinker glider)))
  (is (= (add blinker glider glider2) 
         (set/union blinker glider glider2)))
  (is (= (add "blinker" glider glider2)
         (set/union blinker glider glider2))))

(deftest test-translate []
  (is (= (translate blinker 5 5)
         #{[7 6][7 7][7 8]}))
  (is (= (translate "blinker" 5 5)
         #{[7 6][7 7][7 8]}))
  (is (= (translate blinker -5 -5)
         #{[-3 -4][-3 -3][-3 -2]}))
  (is (= (translate "blinker" -5 -5)
         #{[-3 -4][-3 -3][-3 -2]})))

(deftest test-rotate 
  (is (= (rotate blinker 90)
         #{[1 -2][2 -2][3 -2]})) 
  (is (= (rotate "blinker" 90)
         #{[1 -2][2 -2][3 -2]}))
  (is (= (rotate blinker 180)
         #{[-2 -1][-2 -2][-2 -3]})) 
  (is (= (rotate "blinker" 180)
         #{[-2 -1][-2 -2][-2 -3]}))         
  (is (= (rotate blinker 270)
         #{[-1 2][-2 2][-3 2]})) 
  (is (= (rotate "blinker" 270)
         #{[-1 2][-2 2][-3 2]}))
  (is (= (rotate blinker 90 3 3)
         #{[1 4][2 4][3 4]})) 
  (is (= (rotate "blinker" 90 3 3)
         #{[1 4][2 4][3 4]}))
  (is (= (rotate blinker 180 3 3)
         #{[4 3][4 4][4 5]})) 
  (is (= (rotate "blinker" 180 3 3)
         #{[4 3][4 4][4 5]}))          
  (is (= (rotate blinker 270 3 3)
         #{[3 2][4 2][5 2]})) 
  (is (= (rotate "blinker" 270 3 3)
         #{[3 2][4 2][5 2]})))

(deftest test-flip []
  (is (= (flip blinker :x 3)
         #{[4 1][4 2][4 3]}))
  (is (= (flip "blinker" :x 3)
         #{[4 1][4 2][4 3]}))
  (is (= (flip blinker :y 4)
         #{[2 5][2 6][2 7]}))
  (is (= (flip "blinker" :y 4)
         #{[2 5][2 6][2 7]}))
  (is (= (flip (flip blinker :x 5) :y 5)
         (rotate blinker 180 5 5)))   
  (is (= (flip (flip "blinker" :x 0) :y 0)
         (rotate blinker 180 0 0))))   

(deftest test-pattern! []
  (is (= (create-world [[:add {:pattern blinker}]])
         blinker))
  (is (= (create-world [[:rotate {:pattern blinker :d 90}]])
         (rotate blinker 90)))
  (is (= (create-world [[:translate {:pattern blinker :dx 2 :dy 1}]])
         (translate blinker 2 1)))
  (is (= (create-world [[:flip {:pattern blinker :axis :x :a 4}]])
         (flip blinker :x 4)))
  (is (= (create-world [[:add {:pattern [[:add {:pattern blinker}]
                                         [:add {:pattern glider}]]}]])
         (add blinker glider)))
  (is (= (create-world [[:add {:pattern [:translate {:pattern blinker :dx 2 :dy 0}]}]
                        [:flip {:pattern glider :axis :x :a 5} :as "flipped"]
                        [:rotate {:pattern glider2 :d 90} :as "rotated"]
                        [:translate {:pattern light-spaceship :dx 10 :dy 10}]]))
      (add (add (translate blinker 2 0)
                (flip glider :x 5)
                (rotate glider2 90)
                (translate light-spaceship 10 10))))
  (is (= (create-world [[:add {:pattern [:translate {:pattern [[:add {:pattern blinker}]
                                                               [:add {:pattern glider2}]]
                                                     :dx 10 :dy 10} :as "something"]}
                         [:translate {:pattern "something" :dx -20 :dy -20}]]]))
      (add (store-pattern "something" (add (translate (add blinker glider2) 10 10)))
           (translate "something" -20 -20))))
