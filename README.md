[![CircleCI](https://circleci.com/gh/superstructor/clj-ten-pin-bowling-scoring.svg?style=svg)](https://circleci.com/gh/superstructor/clj-ten-pin-bowling-scoring)
[![GitHub license](https://img.shields.io/github/license/superstructor/clj-ten-pin-bowling-scoring.svg)](LICENSE.txt)

# Ten pin bowling scoring

> "I saw this college team bowling championship. Each team had their own coach. What kind of 
> strategy advice is a bowling coach giving? ‘You know what? This time Timmy, I want you to knock
> down all the pins.’ ‘You sure?’ ‘Trust me. Just do it son!’
>  - Jim Gaffigan

It scores ten pin bowling.

```
$ clj -A:test

Running tests in #{"test"}

Testing ten-pin-bowling-scoring.scorecard-test

Ran 11 tests containing 44 assertions.
0 failures, 0 errors.

$ clj

user=> (require '[ten-pin-bowling-scoring.scorecard :as scorecard])
nil

;; Play a perfect game!

user=>  (reduce scorecard/score (scorecard/create) (concat (repeat 9 [10]) [[10 10 10]]))
{:frames [{:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10], :score 30} {:rolls [10 10 10], :score 30}], :score 300}
```

## Scorecard Data Structure

A scorecard is a map with mappings of:
- `:frames` to a vector of maps. Each map represents a frame with mappings of:
  - `:rolls` to a vector of numbers in the range of 0 to 10 representing each throw of a ball
  - `:score` to a number that is the score for the individual frame. For spares and strikes `:score`
    may not be present if there are not enough subsequent throws yet to calculate the full value.
- `:score` is the total sum of the scores of all the frames. Only present if the game is over (i.e.
  there have been 10 frames thrown).

An example of an incomplete game:

```clojure
{:frames [{:rolls [10]
           :score 20}
          {:rolls [5 5]
           :score 13}
          {:rolls [3 0]
           :score 3}]}
```

1. 1st frame was a strike by knocking down 10 pins in 1 throw
2. 2nd frame was a spare by knocking down 5 pins in 2 throws
3. 3rd frame was an open frame, knocking down 3 pins followed by a gutter ball

## License

Copyright (c) 2019 Isaac Johnston.

Distributed under the Eclipse Public License, the same as Clojure.
