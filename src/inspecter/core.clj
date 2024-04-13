(ns inspecter.core
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [inspecter.selector :as s]
            [com.rpl.specter :refer [recursive-path if-path nthpath before-index continue-then-stay ALL LAST]]))

;; These functions support the matching of Hiccup elements to (limited) CSS
;; selectors. See inspecter.selector/parse-selector for examples of supported selectors.

(defn- as-set [class-str]
  (if (string? class-str)
    (into #{} (remove empty? (str/split class-str #" ")))
    #{}))

(defn- describe
  "Puts an element into a format that can be compared with a selector."
  [[tag {:keys [id class] :as attrs} & _]]
  (-> (name tag)
      (s/parse-selector)
      (update :id (fn [eid] (or eid id)))
      (update :class (fn [cx] (apply conj (as-set class) cx)))
      (assoc :attrs (when (map? attrs)
                      (dissoc attrs :id :class)))))

(def ^:private tokens-equal
  {:wildcard (constantly true)
   :tag      =
   :id       =
   :attrs    =
   :class    #(empty? (set/difference %1 %2))})

(defn- equals
  "Iterates over each token in the selector asking whether it's
   equal to the token provided by the element, using a unique equivalency
   check in `tokens-equal`."
  [selector-tokens el-tokens]
  (->> (map
         (fn [selector-token]
           (let [equal? (get tokens-equal selector-token)]
             (equal? (get selector-tokens selector-token)
                     (get el-tokens selector-token))))
         (keys selector-tokens))
       (every? true?)))

(defn css-matches
  "Indicates whether the selector matches the provided element."
  [selector el]
  (let [parsed (s/parse-selector selector)]
    (equals parsed
            (select-keys (describe el) (keys parsed)))))

;; The public forms below provide support to Specter for finding and transforming
;; elements within Hiccup data.

(def matches
  "Recursively searches the path for elements matching the provided CSS selector.
   See inspecter.selector/parse-selector for supported selectors."
  (recursive-path [selector] path
    (if-path
      vector?
      (if-path
        #(css-matches selector %)
        (continue-then-stay ALL path)
        [ALL path]))))

(def ATTRS
  "Matches an element's attribute map."
  [(if-path
     [(nthpath 1) map?]
     [(nthpath 1) map?]
     (before-index 1))])

(defn update-attrs
  "Assists in transform or setval for Hiccup attributes."
  [f]
  (fn [attrs]
    (f (if (map? attrs) attrs {}))))

(def CONTENTS
  "Matches an element's contents."
  [LAST (complement map?)])