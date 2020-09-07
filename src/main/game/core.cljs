(ns game.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :refer [makeStyles]]))

(def w 40)
(def h 30)
(def timeout 3000)
(def prob 0.2)

(defn cell [x y alive]
  ^{:key (str x "-" y)}
  [:> mui/Box {:height 20 :width 20}
   [:> mui/Paper {:elevation 2
                  :style {:padding 9.5 :margin 0.5
                          :background-color (if alive "#555555" "#eeeeee")}}]])

(defn row [w y py]
  ^{:key (str "row" y)}
  [:> mui/Box {:display "flex" :flex-direction "row"}
   (map #(cell % y (get py %)) (range w))])

(defn field [w h p]
  [:> mui/Container
   (map #(row w % (get p %)) (range h))])

(defn next-p [p]
  (let [get-cell (fn [x y]
                   (if (get-in p [y x]) 1 0))
        neighbors (fn [x y]
                    (apply +
                           (for [dx (range -1 2)
                                 dy (range -1 2)
                                 :when (= 1 (Math/abs (+ dx dy)))]
                             (get-cell (+ x dx) (+ y dy)))))
        next-alive (fn [x y]
                     (let [n (neighbors x y)]
                       (or (= 2 n) (= 3 n))))]
    (->> (for [y (range h) x (range w)]
           (next-alive x y))
         (partition w)
         (mapv #(vec %)))))

(defn main []
  (let [;;p (r/atom (vec (repeat w (vec (repeat h (> 0.75 (rand)))))))
        p (r/atom (mapv #(vec %) (partition w (repeatedly (* w h) #(> prob (rand))))))]
    (fn []
      (js/setTimeout #(swap! p next-p) timeout)
      [field w h @p])))

(defn init []
  (rdom/render [main] (js/document.getElementById "app")))

(init)

