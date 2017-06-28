(ns cellular-audiomata.pattern-test
  (:require [clojure.set :as set]
            [clojure.test :refer :all]
            [cellular-audiomata.pattern :refer :all]))
  
(defn pattern-test-fixture [f]
  (clear-registry)
  (store-pattern "blinker" blinker)
  (store-pattern "glider" glider)
  (f))

(use-fixtures :each pattern-test-fixture)

(deftest test-store-pattern []
  (is (= (store-pattern "blinker" blinker)
         blinker)))

(deftest test-add []
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
