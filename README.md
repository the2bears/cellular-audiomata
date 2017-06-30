# cellular-audiomata

This will be a test at first, to answer a question I've had for a while: Can Conway's Life be used as a music sequencer?

In order to do a Life sequencer, you need a Life engine: [conway.clj](https://github.com/the2bears/cellular-audiomata/blob/master/src/cellular_audiomata/conway.clj)

The engine is simple, creating content is almost always the hard part. This is my first try at a DSL, but here is it: [pattern.clj](https://github.com/the2bears/cellular-audiomata/blob/master/src/cellular_audiomata/pattern.clj). Note this is still very early and likely will change a lot.

The examples below return sets of x,y tuples such as:

```clojure
(def blinker #{[2 1] [2 2] [2 3]})
```

Some examples:
```clojure
;note that the blinker, glider, glider2, and light-spaceship symbols are references to already defined sets
(create-world [[:add {:pattern [:translate {:pattern blinker :dx 2 :dy 0}]}]
               [:flip {:pattern glider :axis :x :a 5} :as "flipped"]
               [:rotate {:pattern glider2 :d 90} :as "rotated"]
               [:translate {:pattern light-spaceship :dx 10 :dy 10}]]))

;nested patterns are possible, and named patterns are available for reference later
(create-world [[:add {:pattern [:translate {:pattern [[:add {:pattern blinker}]
                                                      [:add {:pattern glider2}]]
                                            :dx 10 :dy 10}]} :as "something"]
               [:translate {:pattern "something" :dx -20 :dy -20}]]))
```
In addition to the DSL, the API can also be used to create complex patterns:
```clojure
(-> blinker
    (rotate 90)
    (translate 5 1)
    (flip :x 6))
```

## License

Copyright © 2016-2017 William Swaney

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
