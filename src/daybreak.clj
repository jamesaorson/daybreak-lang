#!/usr/bin/env bb
(ns daybreak
  (:require [daybreak.frontend :as frontend]))

(defn -main [& args]
  (if (some #(= % "--only-download-deps") args)
    (System/exit 0)
    (frontend/hello)))

(when (= *file* (System/getProperty "babashka.file"))
  (-main *command-line-args*))
