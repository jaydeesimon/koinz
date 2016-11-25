(ns koinz.core
  (:require ))

(enable-console-print!)

(println "This text is printed from src/koinz/core.cljs. Go ahead and edit it and see reloading in action.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def colors [:r :g :b])

(def rows 17)
(def cols 14)

(defn rand-row [row-vals cols]
  (mapv #(rand-nth row-vals) (range cols)))

(defn generate-board [rows cols]
  (let [top-rows (for [_ (range (/ rows 2))]
                   (vec (repeat cols :.)))
        bottom-rows (for [_ (range (/ rows 2))]
                      (rand-row colors cols))]
    (vec (concat top-rows bottom-rows))))

(defn generate-board' [rows cols]
  (repeatedly rows #(rand-row (concat colors [:.]) cols)))

(defn generate-board'' [rows cols]
  (vec (repeat rows (vec (repeat cols :r)))))

(defn transpose [m]
  (apply map vector m))

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
