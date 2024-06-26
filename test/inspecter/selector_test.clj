(ns inspecter.selector-test
  (:require [clojure.test :refer :all]
            [inspecter.selector :as s]))

(deftest parse-selector-test
  (are [x y]
    (= x y)
    {:wildcard true} (s/parse-selector "*")
    {:tag :div} (s/parse-selector "div")
    {:class #{"hello" "world"}} (s/parse-selector ".hello.world")
    {:tag :div :class #{"hello"}} (s/parse-selector "div.hello")
    {:tag :div :class #{"hello" "world"}} (s/parse-selector "div.hello.world")
    {:id "hello"} (s/parse-selector "#hello")
    {:tag :div :id "hello"} (s/parse-selector "div#hello")
    {:tag :div :attrs {:hi nil}} (s/parse-selector "div[hi]")
    {:tag   :div
     :id    "test"
     :class #{"first-class" "second-class"}
     :attrs {:hi      nil
             :hello   "world"
             :goodbye "cruel world"}}
    (s/parse-selector "div#test.first-class.second-class[hi][hello=world][goodbye=\"cruel world\"]")))
