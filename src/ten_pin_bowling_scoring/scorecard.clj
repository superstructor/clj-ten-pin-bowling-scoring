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

(defn spare?
  "Returns true if rolls is a spare, otherwise false."
  [rolls]
  (and
    (<= 2 (count rolls) 3)
    (= 10 (+ (first rolls)
             (second rolls)))))

(defn score-frame
  "Returns a new map representing a single frame's rolls and score in a game of
   ten pin bowling. For strikes and spares uses index to look forward in
   all-rolls to determine the full value."
  [all-rolls index rolls]
  (let [frame-score (reduce + rolls)]
    (cond
      (strike? rolls)
      (if (and (= 9 index)
               (second rolls)
               (nth rolls 2 nil))
        ;; Strike in last frame...
        {:rolls rolls
         :score frame-score}
        (let [next-rolls (nth all-rolls (inc index) nil)
              nnext-rolls (nth all-rolls (inc (inc index)) nil)
              second-ball (first next-rolls)
              third-ball (or (second next-rolls) (first nnext-rolls))]
          (if (and second-ball third-ball)
            ;; Strike is 10 + next two rolls.
            {:rolls rolls
             :score (+ 10 second-ball third-ball)}
            ;; Strike is not complete yet, so does not have a score.
            {:rolls rolls})))

      (spare? rolls)
      (if (and (= 9 index)
               (second rolls)
               (nth rolls 2 nil))
        ;; Spare in last frame...)
        {:rolls rolls
         :score frame-score}
        (if-let [next-rolls (nth all-rolls (inc index) nil)]
          ;; Spare is 10 + first roll of next frame.
          {:rolls rolls
           :score (+ 10 (first next-rolls))}
          ;; Spare is not complete yet, so does not have a score.
          {:rolls rolls}))

      :open-frame
      {:rolls rolls
       :score frame-score})))

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
    (let [all-rolls (conj (mapv :rolls frames) rolls)
          frames' (->> all-rolls
                       (map-indexed (partial score-frame all-rolls))
                       (vec))
          scorecard' {:frames frames'}]
      (cond-> scorecard'
              (over? scorecard')
              (assoc :score (total frames'))))))