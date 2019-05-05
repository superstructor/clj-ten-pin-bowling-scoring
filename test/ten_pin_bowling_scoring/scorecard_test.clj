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
        "a full game has a total score of zero and all frame scores are zero"))
  (testing "a game of ones"
    (is (= {:frames (repeat 2 {:rolls [1 1]
                               :score 2})}
           (reduce scorecard/score (scorecard/create) (repeat 2 [1 1])))
        "a partial game does not have a total score and all frame scores are two")
    (is (= {:frames (repeat 10 {:rolls [1 1]
                                :score 2})
            :score  20}
           (reduce scorecard/score (scorecard/create) (repeat 10 [1 1])))
        "a full game has a total score of twenty and all frame scores are two"))
  (testing "a game containing a spare"
    (is (= {:frames (into [{:rolls [5 5]
                            :score 19}
                           {:rolls [9 0]
                            :score 9}]
                          (repeat 8 {:rolls [0 0]
                                     :score 0}))
            :score  28}
           (reduce scorecard/score (scorecard/create) (into [[5 5] [9 0]] (repeat 8 [0 0]))))
        "a full game of one spare followed by nine followed by gutter balls has a total score of
         twenty eight"))
  (testing "a game containing a strike"
    (is (= {:frames (into [{:rolls [10]
                            :score 19}
                           {:rolls [5 4]
                            :score 9}]
                          (repeat 8 {:rolls [0 0]
                                     :score 0}))
            :score  28}
           (reduce scorecard/score (scorecard/create) (into [[10] [5 4]] (repeat 8 [0 0]))))
        "a full game of one strike followed by a five, then a four and subsequently gutter balls
         has a total score of twenty eight")
    (is (= {:frames [{:rolls [10]}]}
           (reduce scorecard/score (scorecard/create) [[10]]))
        "a partial game does not have a total score nor a score for an incomplete frame"))
  (testing "a perfect game"
    (is (= {:frames (-> (repeat 9 {:rolls [10]
                                   :score 30})
                        (vec)
                        (conj {:rolls [10 10 10]
                               :score 30}))
            :score 300}
           (reduce scorecard/score (scorecard/create) (concat (repeat 9 [10]) [[10 10 10]])))
        "a full game has a total score of three hundred and all frame scores are thirty")))