(ns koinz.ui
  (:require [cljsjs.fabric]
            [koinz.core :as k]))

(enable-console-print!)

(defn circle [& {:keys [radius fill left top]}]
  (fabric.Circle. (clj->js {:radius radius
                            :fill   (str fill)
                            :left left
                            :top top})))

(defn draw-board [board]
  (let [canvas (fabric.StaticCanvas. "c")
        rows (count board)
        cols (count (first board))
        width (/ (.-width canvas) cols)
        height (/ (.-height canvas) rows)]
    (doseq [row (range rows)
            col (range cols)]
      (when (not= (get-in board [row col]) :.)
        (let [shape (circle :radius (/ width 2.5)
                            :fill (-> (get-in board [row col]) :color name)
                            :left (* width col)
                            :top (* height row))]
          (.add canvas shape))))))