#!/usr/bin/env bb
  (ns daybreak
    (:require [babashka.fs :as fs]
              [daybreak.frontend :as frontend]
              [daybreak.programs :as programs]))

(defn -main [& args]
  (if (some #(= % "--only-download-deps") args)
    (System/exit 0)
    (when (some #(= % "--test") args)
      (let [examples (fs/list-dir "examples")]
        (doseq [example examples]
          (let [files (fs/list-dir example)]
            (doseq [file files]
              (frontend/run file))))))))

(when (= *file* (System/getProperty "babashka.file"))
  (-main *command-line-args*))
