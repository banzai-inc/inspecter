(ns inspecter.selector
  (:require [instaparse.core :as insta]))

(def selector-parser
  (insta/parser
    "<selector>         = wildcard?|(tag? id? class* attrs*)
     <name>             = #'[A-Za-z0-9\\-\\_]+'
     <text>             = #'[A-Za-z0-9\\-\\_\\s]+'
     attr-name          = name
     attr-value         = text
     wildcard           = <'*'>
     <attrs-with-value> = <'['> attr-name <'='> (attr-value | <'\"'> attr-value <'\"'>) <']'>
     attrs              = attrs-with-value | <'['> attr-name <']'>
     id                 = <'#'> name
     class              = <'.'> name
     tag                = name"))

(defn- update-selectors [tags-n-fns]
  (fn [memo vals]
    (let [[k] vals]
      (if-let [update-fn (get tags-n-fns k)]
        (assoc memo k (update-fn (get memo k) (rest vals)))
        (throw (ex-info (str "There is no update function for " k ".") {}))))))

(defn- conj-to-set
  "Conj's the items into a set."
  [memo new-val]
  (apply conj (or memo #{}) new-val))

(defn- take-newest
  "Takes the most recent provided value if there are more than one."
  [_ new-val]
  (first new-val))

(defn- collapse-attrs
  [memo vals]
  (merge
    (or memo {})
    (let [{:keys [attr-name attr-value]}
          (reduce
            (fn [acc [k v]]
              (assoc acc k v))
            {}
            vals)]
      {(keyword attr-name) attr-value})))

(def ^:private updates
  {:wildcard (constantly true)
   :tag      (comp keyword take-newest)
   :attrs    collapse-attrs
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