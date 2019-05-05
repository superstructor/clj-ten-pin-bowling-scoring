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

(deftest total-test
  (testing "sum of the frames' scores"
    (is (zero? (scorecard/total (repeat 10 {:rolls [0 0] :score 0})))
        "zero for a gutter game")
    (is (= 20 (scorecard/total (repeat 10 {:rolls [1 1] :score 2})))
        "twenty for a game of ones")))

(deftest over?-test
  (testing "true if the game is over"
    (is (scorecard/over?
          (reduce scorecard/score (scorecard/create)
                  (repeat 10 [0 0])))
        "a gutter game")
    (is (scorecard/over?
          (reduce scorecard/score (scorecard/create)
                  (concat (repeat 9 [10]) [[10 10 10]])))
        "a perfect game"))
  (testing "false if the game is under"
    (is (not (scorecard/over?
               (reduce scorecard/score (scorecard/create)
                       (repeat 9 [0 0]))))
        "a gutter game")
    (is (not (scorecard/over?
               (reduce scorecard/score (scorecard/create)
                       (repeat 9 [10]))))
        "a near-perfect game")))

(deftest strike?-test
  (testing "true if rolls is a strike"
    (is (scorecard/strike? [10])))
  (testing "false if rolls is not a strike"
    (is (not (scorecard/strike? [9])))
    (is (not (scorecard/strike? [7 3])))))

(deftest spare?-test
  (testing "true if rolls is a spare"
    (is (scorecard/spare? [5 5])
        "not the last frame")
    (is (scorecard/spare? [5 5 10])
        "last frame"))
  (testing "false if rolls is not a spare"
    (is (not (scorecard/spare? [0 0]))
        "gutter balls")
    (is (not (scorecard/spare? [10]))
        "a strike")))

(deftest last-frame?
  (testing "true if index of last frame"
    (is (scorecard/last-frame? 9)))
  (testing "false if not index of last frame"
    (is (not (scorecard/last-frame? 8)))))

(deftest third
  (testing "like (first (nnext x))..."
    (is (nil? (scorecard/third [1 2])))
    (is (= 3 (scorecard/third [1 2 3 4])))))

(deftest score-strike
  (testing "an incomplete strike"
    (is (= {:rolls [10]}
           (scorecard/score-strike [[10]] 0 [10]))))
  (testing "a strike followed by gutter balls"
    (is (= {:rolls [10]
            :score 10}
           (scorecard/score-strike [[10] [0 0]] 0 [10]))))
  (testing "a strike followed by a spare"
    (is (= {:rolls [10]
            :score 20}
           (scorecard/score-strike [[10] [5 5]] 0 [10]))))
  (testing "a strike followed by strikes"
    (is (= {:rolls [10]
            :score 30}
           (scorecard/score-strike [[10] [10] [10]] 0 [10])))))

(deftest score-spare
  (testing "an incomplete spare"
    (is (= {:rolls [5 5]}
           (scorecard/score-spare [[5 5]] 0 [5 5]))))
  (testing "a spare followed by gutter balls"
    (is (= {:rolls [5 5]
            :score 10}
           (scorecard/score-spare [[5 5] [0 0]] 0 [5 5]))))
  (testing "a spare followed by a spare"
    (is (= {:rolls [5 5]
            :score 15}
           (scorecard/score-spare [[5 5] [5 5]] 0 [5 5]))))
  (testing "a spare followed by a strike"
    (is (= {:rolls [5 5]
            :score 20}
           (scorecard/score-spare [[5 5] [10]] 0 [5 5])))))

(deftest score-frame
  (testing "a gutter frame"
    (is (= {:rolls [0 0]
            :score 0}
           (scorecard/score-frame [[0 0]] 0 [0 0]))))
  (testing "a strike frame"
    (is (= {:rolls [10]
            :score 15}
           (scorecard/score-frame [[10] [3 2]] 0 [10])))))

(deftest score-test
  (testing "invalid arguments"
    (let [e (reduce scorecard/score (scorecard/create) (repeat 11 [0 0]))]
      (is (and (ex-data e)
               (= (.getMessage e) "Game is already over!"))
          "can not keep rolling balls in a game that is over!"))
    (let [e (reduce scorecard/score (scorecard/create) [[10 10]])]
      (is (and (ex-data e)
               (= (.getMessage e) "Cannot roll after strike in the same frame unless last frame."))))
    (let [e (reduce scorecard/score (scorecard/create) [[5 5 10]])]
      (is (and (ex-data e)
               (= (.getMessage e) "Cannot roll after spare in the same frame unless last frame.")))))
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
         twenty eight")
    (is (= {:frames [{:rolls [5 5]}]}
           (reduce scorecard/score (scorecard/create) [[5 5]]))
        "a partial game does not have a total score nor a score for an incomplete frame")
    (is (= {:frames (concat
                      (repeat 9 {:rolls [5 4]
                                 :score 9})
                      [{:rolls [5 5 5]
                        :score 15}])
            :score  96}
           (reduce scorecard/score (scorecard/create) (concat (repeat 9 [5 4]) [[5 5 5]])))
        "a full game of open frames followed by a spare in the last frame"))
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
            :score  300}
           (reduce scorecard/score (scorecard/create) (concat (repeat 9 [10]) [[10 10 10]])))
        "a full game has a total score of three hundred and all frame scores are thirty")))