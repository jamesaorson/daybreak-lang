#!/usr/bin/env bb
(ns daybreak
  (:require [daybreak.frontend :as frontend]
            [daybreak.programs :as programs]))

(defn -main [& args]
  (if (some #(= % "--only-download-deps") args)
    (System/exit 0)
    (for [program [programs/HELLO-WORLD]]
      (frontend/run-source program))))

(when (= *file* (System/getProperty "babashka.file"))
  (-main *command-line-args*))
