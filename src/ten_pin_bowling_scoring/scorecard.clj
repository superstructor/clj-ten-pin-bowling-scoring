(ns ten-pin-bowling-scoring.scorecard)

(defn create
  "Returns a new map representing a ten pin bowling scorecard; i.e. the initial
   state of the game."
  []
  {:frames []})

(defn total
  "Returns the sum of all the frames' scores."
  [frames]
  (reduce + (map :score frames)))

(defn over?
  "Returns true if all frames have been thrown, otherwise false."
  [{:keys [frames] :as scorecard}]
  (= 10 (count frames)))

(defn strike?
  "Returns true if rolls is a strike, otherwise false."
  [rolls]
  (= 10 (first rolls)))

(defn score
  "Returns a new map representing a ten pin bowling scorecard with the rolls for
   a single frame 'added' and frames' scores recalculated. If the game is over a
   final score will also be provided."
  [{:keys [frames] :as scorecard} rolls]
  (cond
    (over? scorecard)
    (ex-info "Game is already over!" {})

    (and (not= 9 (count frames))
         (strike? rolls)
         (< 1 (count rolls)))
    (ex-info "Cannot roll after strike in the same frame unless last frame." {})

    :default
    (let [frames-rolls (conj (mapv :rolls frames) rolls)
          frames' (->> frames-rolls
                       (map-indexed
                         (fn [index frame-rolls]
                           (let [frame-score (reduce + frame-rolls)]
                             (cond
                               (strike? frame-rolls)
                               (if (and (= 9 index)
                                        (second frame-rolls)
                                        (nth frame-rolls 2 nil))
                                 ;; Strike in last frame...
                                 {:rolls frame-rolls
                                  :score frame-score}
                                 (let [next-rolls (nth frames-rolls (inc index) nil)
                                       nnext-rolls (nth frames-rolls (inc (inc index)) nil)
                                       second-ball (first next-rolls)
                                       third-ball (or (second next-rolls) (first nnext-rolls))]
                                   (if (and second-ball third-ball)
                                     ;; Strike is 10 + next two rolls.
                                     {:rolls frame-rolls
                                      :score (+ 10 second-ball third-ball)}
                                     ;; Strike is not complete yet, so does not have a score.
                                     {:rolls frame-rolls})))

                               (= 10 frame-score)
                               (if (and (= 9 index)
                                        (second frame-rolls)
                                        (nth frame-rolls 2 nil))
                                 ;; Spare in last frame...)
                                 {:rolls frame-rolls
                                  :score frame-score}
                                 (if-let [next-rolls (nth frames-rolls (inc index) nil)]
                                   ;; Spare is 10 + first roll of next frame.
                                   {:rolls frame-rolls
                                    :score (+ 10 (first next-rolls))}
                                   ;; Spare is not complete yet, so does not have a score.
                                   {:rolls frame-rolls}))

                               :open-frame
                               {:rolls frame-rolls
                                :score frame-score}))))
                       (vec))
          scorecard' {:frames frames'}]
      (cond-> scorecard'
              (over? scorecard')
              (assoc :score (total frames'))))))