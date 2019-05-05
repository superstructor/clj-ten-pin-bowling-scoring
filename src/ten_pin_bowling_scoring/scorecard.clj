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
  (let [frame-score (reduce + rolls)
        frame {:rolls rolls
               :score frame-score}
        frames (conj (:frames scorecard) frame)
        score (reduce + (map :score frames))]
    {:frames frames
     :score  score}))