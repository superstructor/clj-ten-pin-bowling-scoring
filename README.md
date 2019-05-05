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

## License

Copyright (c) 2019 Isaac Johnston.

Distributed under the Eclipse Public License, the same as Clojure.
