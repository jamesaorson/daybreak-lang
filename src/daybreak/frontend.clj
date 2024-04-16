(ns daybreak.frontend
  (:require [daybreak.lex :as lex]))

(defn run [program]
  (lex/lex
   program))
