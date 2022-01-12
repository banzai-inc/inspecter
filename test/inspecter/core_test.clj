(ns inspecter.core-test
  (:require [clojure.test :refer :all]
            [inspecter.core :as inspect]
            [com.rpl.specter :as specter])
  (:refer-clojure :exclude [find]))

(def hiccup
  [:div#one.find-me {:found "me"}
   [:div
    [:h1 {} "More to say."]
    [:a#two.find-me {:href "https://www.google.com"}
     "A link!"]
    [:div#three.find-me "Without attributes."]
    [:div#four.find-me {:without "contents."}]
    "Some more content."]])

(defn- includes? [selector els]
  (boolean (some (partial inspect/css-matches selector) els)))

(deftest select-tests
  (let [els (specter/select [(inspect/matches :.find-me)] hiccup)]
    (is (includes? :div#one els))
    (is (includes? :a#two els))
    (is (includes? :div#three els))
    (is (includes? :div#four els))
    (is (not (includes? :h1 els)))))

(deftest select-attrs-tests
  (is (= [{:href "https://www.google.com"}
          {:without "contents."}
          {:found "me"}]
         (specter/select [(inspect/matches :.find-me) inspect/ATTRS] hiccup))))

(deftest select-contents-tests
  (let [[c1 c2 c3] (specter/select [(inspect/matches :.find-me) inspect/CONTENTS] hiccup)]
    (is (string? c1))
    (is (string? c2))
    (is (vector? c3))))

(deftest transform-test
  (let [els (->> (specter/transform
                   [(inspect/matches :.find-me) inspect/ATTRS]
                   (inspect/update-attrs #(assoc % :changed :me))
                   hiccup)
                 (specter/select [(inspect/matches :.find-me) inspect/ATTRS]))]
    (is (every? #(= :me (:changed %)) els))))

