#!/usr/bin/env bb
  (ns daybreak
    (:require [babashka.fs :as fs]
              [daybreak.frontend :as frontend]))

(defn- test []
  (let [examples (fs/list-dir "examples")]
    (doseq [example examples]
      (let [files (fs/list-dir example)]
        (doseq [file files]
          (frontend/run file))))))

(defn -main [& args]
  (cond
    (some #(= % "--only-download-deps") args) (System/exit 0)
    (some #(= % "--test") args) (test)))

(when (= *file* (System/getProperty "babashka.file"))
  (-main *command-line-args*))
