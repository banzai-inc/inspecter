(ns inspecter.core-test
  (:require [clojure.test :refer :all]
            [inspecter.core :as inspect]
            [com.rpl.specter :as specter])
  (:refer-clojure :exclude [find]))

(def hiccup
  [:div#one.find-me {:found "me"}
   [:div
    [:h1 {:data-find "me"} "More to say."]
    [:a#two.find-me {:href "https://www.google.com"} "A link!"]
    [:div#three.find-me "Without attributes."
     [:a "another link"]]
    [:div#four.find-me {:without "contents."}]
    "Some more content."]])

(defn- includes? [selector els]
  (boolean (some (partial inspect/css-matches selector) els)))

(deftest select-tests
  (testing "wildcard match"
    (is (= 7 (count (specter/select [(inspect/matches "*")] hiccup)))))

  (testing "class match"
    (let [els (specter/select [(inspect/matches ".find-me")] hiccup)]
      (is (includes? "div#one" els))
      (is (includes? "a#two" els))
      (is (includes? "div#three" els))
      (is (includes? "div#four" els))
      (is (not (includes? "h1" els)))))

  (testing "attribute match"
    (is (seq (specter/select [(inspect/matches "h1[data-find=me]")] hiccup)))
    (is (empty? (specter/select [(inspect/matches "h1[data-find=you]")] hiccup)))))

(deftest select-attrs-tests
  (is (= [{:href "https://www.google.com"}
          {:without "contents."}
          {:found "me"}]
         (specter/select [(inspect/matches ".find-me") inspect/ATTRS] hiccup))))

(deftest select-contents-tests
  (let [[c1 c2 c3] (specter/select [(inspect/matches ".find-me") inspect/CONTENTS] hiccup)]
    (is (string? c1))
    (is (vector? c2))
    (is (vector? c3))))

(deftest transform-test
  (testing "update attributes"
    (let [els (->> (specter/transform
                     [(inspect/matches ".find-me") inspect/ATTRS]
                     (inspect/update-attrs #(assoc % :changed :me))
                     hiccup)
                   (specter/select [(inspect/matches ".find-me") inspect/ATTRS]))]
      (is (every? #(= :me (:changed %)) els))))

  (testing "replace element"
    (let [els (specter/setval
                [(inspect/matches "h1")]
                [:div.replaced "I've been replaced!"]
                hiccup)]
      (is (seq (specter/select [(inspect/matches "div.replaced")] els))))))