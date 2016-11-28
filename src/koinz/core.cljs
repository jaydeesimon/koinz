(ns koinz.core
  (:require [cljs.core :as c]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def colors [:red :green :blue])

(def rows 17)
(def cols 14)

(defn generate-coin
  ([] (generate-coin (rand-nth colors)))
  ([color] {:id (c/random-uuid) :color color}))

(defn generate-board [rows cols]
  (let [top (repeat (/ rows 2) (vec (repeat cols :.)))
        bottom (repeatedly (/ rows 2) #(vec (repeatedly cols generate-coin)))]
    (vec (concat top bottom))))

(defn generate-board' [rows cols]
  (let [cols-fn (fn [] ((rand-nth [generate-coin (constantly :.)])))]
    (vec (repeatedly rows #(vec (repeatedly cols cols-fn))))))

(defn transpose [m]
  (apply mapv vector m))

(defn fall [board]
  (->> (transpose board)
       (map #(sort-by (fn [v] (if (= v :.) -1 0)) %))
       (transpose)))

;; Bug: If board is not a vector of vectors, we get an infinite loop
(defn chain [board [row col]]
  (loop [chain #{[row col]}
         q [[row col]]
         seen #{}]
    (if (empty? q)
      chain
      (let [coord (first q)
            neighbor-coords [[0 1] [0 -1] [1 0] [-1 0]]
            neighbors (map #(mapv + % coord) neighbor-coords)
            like-neighbors (filter #(= (get-in board [row col]) (get-in board %)) neighbors)
            seen (conj seen coord)]
        (recur (apply conj chain like-neighbors)
               (apply conj (rest q) (remove seen like-neighbors))
               seen)))))

(defn board-map [board]
  (->> (for [row (range (count board))
             col (range (count (first board)))]
         [row col])
       (remove #(= :. (get-in board %)))
       (reduce #(assoc %1 (get-in board %2) %2) {})))

(defn transitions [board0 board1]
  (->> (merge-with vector (board-map board0) (board-map board1))
       (filter (fn [[_ [start end]]] (not= start end)))))
