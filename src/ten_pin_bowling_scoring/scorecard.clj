(ns ten-pin-bowling-scoring.scorecard)

(defn create
  "Returns a new map representing a ten pin bowling scorecard; i.e. the initial
   state of the game."
  []
  {:frames []})

(defn score
  "Returns a new map representing a ten pin bowling scorecard with the rolls for
   a single frame 'added' and frames' scores recalculated. If the game is over a
   final score will also be provided."
  [scorecard rolls]
  (let [frames-rolls (conj (mapv :rolls (:frames scorecard))
                           rolls)
        frames (->> frames-rolls
                   (map-indexed
                     (fn [index frame-rolls]
                       (let [frame-score (reduce + frame-rolls)]
                         (if (= 10 frame-score)
                           (if-let [next-rolls (nth frames-rolls (inc index) nil)]
                             ;; Spare is 10 + first roll of next frame.
                             {:rolls frame-rolls
                              :score (+ 10 (first next-rolls))}
                             ;; Spare is not complete yet, so does not have a score.
                             {:rolls frame-rolls})
                           ;; Not a spare...
                           {:rolls frame-rolls
                            :score frame-score}))))
                   (vec))]
    (cond-> {:frames frames}
            (= 10 (count frames))
            (assoc :score (reduce + (map :score frames))))))