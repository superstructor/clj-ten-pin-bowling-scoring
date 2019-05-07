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

(defn last-frame?
  "Returns true if zero-based index of last frame (i.e. 9), otherwise false."
  [index]
  (= 9 index))

(defn third
  "Like (first (nnext x))."
  [coll]
  (nth coll 2 nil))

(defn score-strike
  "Returns a new map representing a single frame's rolls and score that is a
   strike in a game of ten pin bowling. Uses index to look forward in all-rolls
   to determine the full value of the strike, if possible."
  [all-rolls index rolls]
  (let [next-rolls (nth all-rolls (inc index) nil)
        nnext-rolls (nth all-rolls (inc (inc index)) nil)
        second-roll (first next-rolls)
        third-roll (or (second next-rolls) (first nnext-rolls))]
    (if (and second-roll third-roll)
      ;; Strike is 10 + next two rolls.
      {:rolls rolls
       :score (+ 10 second-roll third-roll)}
      ;; Strike is not complete yet, so does not have a score.
      {:rolls rolls})))

(defn score-spare
  "Returns a new map representing a single frame's rolls and score that is a
   spare in a game of ten pin bowling. Uses index to look forward in all rolls
   to determine the full value of the spare, if possible."
  [all-rolls index rolls]
  (if-let [next-rolls (nth all-rolls (inc index) nil)]
    ;; Spare is 10 + first roll of next frame.
    {:rolls rolls
     :score (+ 10 (first next-rolls))}
    ;; Spare is not complete yet, so does not have a score.
    {:rolls rolls}))

(defn score-frame
  "Returns a new map representing a single frame's rolls and score in a game of
   ten pin bowling. For strikes and spares uses index to look forward in
   all-rolls to determine the full value, if possible."
  [all-rolls index rolls]
  (cond
    (and (strike? rolls)
         (not (last-frame? index)))
    (score-strike all-rolls index rolls)

    (and (spare? rolls)
         (not (last-frame? index)))
    (score-spare all-rolls index rolls)

    ;; Open frame OR last frame
    :default
    {:rolls rolls
     :score (reduce + rolls)}))

(defn error
  "Returns an instance of ExceptionInfo if 'adding' rolls to scorecard would be
   an invalid game state, otherwise nil."
  [{:keys [frames] :as scorecard} rolls]
  (let [next-index (count frames)]
    (cond
      (over? scorecard)
      (ex-info "Game is already over!" {})

      (and (not (last-frame? next-index))
           (strike? rolls)
           (< 1 (count rolls)))
      (ex-info "Cannot roll after strike in the same frame unless last frame." {})

      (and (not (last-frame? next-index))
           (spare? rolls)
           (< 2 (count rolls)))
      (ex-info "Cannot roll after spare in the same frame unless last frame." {})

      (and (last-frame? next-index)
           (or (strike? rolls) (spare? rolls))
           (not= 3 (count rolls)))
      (ex-info "Must roll three balls in the last frame if it is a strike or spare." {})

      (and (not (strike? rolls))
           (not (and (spare? rolls) (last-frame? next-index)))
           (not= 2 (count rolls)))
      (ex-info "Must roll two balls unless a strike in any frame or a spare in the last frame." {})

      (let [sum (reduce + rolls)]
        (and (not (last-frame? next-index))
             (not (<= 0 sum 10))))
      (ex-info "Sum of rolls in a frame must be in the range of 0 to 10. There are only 10 pins!" {}))))

(defn score
  "Returns a new map representing a ten pin bowling scorecard with the rolls for
   a single frame 'added' and frames' scores recalculated. If the game is over a
   final score will also be provided."
  [{:keys [frames] :as scorecard} rolls]
  (if-let [e (error scorecard rolls)]
    e
    (let [all-rolls (conj (mapv :rolls frames) rolls)
          f (partial score-frame all-rolls)
          frames' (->> all-rolls (map-indexed f) (vec))
          scorecard' {:frames frames'}]
      (cond-> scorecard'
              (over? scorecard')
              (assoc :score (total frames'))))))