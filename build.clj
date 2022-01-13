(ns build
  (:require [clojure.tools.build.api :as b])
  (:refer-clojure :exclude [test]))

; clojure -T:build test
(defn test [_]
  (let [basis (b/create-basis {:aliases [:test]})
        cmds (b/java-command {:basis     basis
                              :main      'clojure.main
                              :main-args ["-m" "cognitect.test-runner"]})
        {:keys [exit]} (b/process cmds)]
    (when-not (zero? exit)
      (throw (ex-info "Tests failed" {})))))

(def lib 'com.teachbanzai/inspecter)
(def version (format (slurp "./version") (b/git-count-revs nil)))
(def src-dirs ["src"])
(def path "target")
(def class-dir (str path "/classes"))

(defn jar
  "Creates a JAR."
  [_]
  (let [basis (b/create-basis {:project "deps.edn"})
        jar-file (format "target/%s.jar" (name lib))]
    (b/delete {:path path})
    (b/write-pom {:class-dir class-dir
                  :basis     basis
                  :lib       lib
                  :version   version
                  :src-dirs  src-dirs})
    (b/copy-dir {:src-dirs   src-dirs
                 :target-dir class-dir})
    (b/jar {:class-dir class-dir
            :jar-file  jar-file})))