(ns daybreak.frontend
  (:require [daybreak.lex :as lex]
            [net.clojars.jamesaorson.brain.core :as brain]))

(defn- msg-handler [{:keys [data-bag msg msg-handler]}]
  (let [{:keys [source]} data-bag]
    (lex/lex
     source)))

(defn run-source [program]
  (brain/spark msg-handler {:source program}))

(defn run [file]
  (run-source (slurp file)))
