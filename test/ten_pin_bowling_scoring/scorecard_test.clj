(ns ten-pin-bowling-scoring.scorecard-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [ten-pin-bowling-scoring.scorecard :as scorecard]))

(deftest create-test
  ;; NOTE I also considered calling this scorecard/empty but did not want to
  ;; override, and thus refer-exclude, clojure.core/empty.
  (testing "creates an empty scorecard"
    (is (= {:frames []}
           (scorecard/create)))))

(deftest score-test
  (testing "a gutter game"
    (is (= {:frames (repeat 2 {:rolls [0 0]
                               :score 0})}
           (reduce scorecard/score (scorecard/create) (repeat 2 [0 0])))
        "a partial game does not have a total score and all frame scores are zero")
    (is (= {:frames (repeat 10 {:rolls [0 0]
                                :score 0})
            :score  0}
           (reduce scorecard/score (scorecard/create) (repeat 10 [0 0])))
        "a full game has a total score of zero and all frame scores are zero")))