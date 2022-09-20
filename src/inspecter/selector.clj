(ns inspecter.selector
  (:require [instaparse.core :as insta]))

(def selector-parser
  (insta/parser
    "<selector> = wildcard?|(tag? id? class*)
     <name>     = #'[A-Za-z0-9\\-\\_]+'
     wildcard   = <'*'>
     id         = <'#'> name
     class      = <'.'> name
     tag        = name"))

(defn- update-selectors [tags-n-fns]
  (fn [memo [k new-val]]
    (if-let [update-fn (get tags-n-fns k)]
      (assoc memo k (update-fn (get memo k) new-val))
      (throw (ex-info (str "There is no update function for " k ".") {})))))

(defn- conj-to-set
  "Conj's the items into a set."
  [old-val new-val]
  (if (set? old-val) (conj old-val new-val) #{new-val}))

(defn- take-newest
  "Takes the most recent provided value if there are more than one."
  [_ new-val]
  new-val)

(def ^:private updates
  {:wildcard (constantly true)
   :tag      (comp keyword take-newest)
   :id       take-newest
   :class    conj-to-set})

(defn parse-selector
  "Returns a tokenized representation of the (limited) CSS selector. The
   following queries are supported:

   \"div\" => {:tag :div}
   \"div#hello\" => {:tag :div :id \"hello\"}
   \"div.hello.world\" => {:tag :div :class #{\"hello\" \"world\"}}"
  [selector]
  (->> selector
       (selector-parser)
       (reduce (update-selectors updates) {})))