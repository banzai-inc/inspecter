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
    {:tag :div :id "hello"} (s/parse-selector "div#hello")))
