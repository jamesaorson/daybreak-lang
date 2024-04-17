(ns daybreak.lex
  (:require [clojure.core.match :refer [match]]
            [daybreak.ast :as ast]))

(defn lex
  [input]
  (println "Lexing:\n" input))

(defmacro make-msg-lex [lexer]
  {:kind :lex
   :lexer lexer})

(defmacro make-msg-lex-done [token-stream]
  {:kind :lex-done
   :token-stream token-stream})

(defn msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :lex
      :lexer _}] {:running? true
                  :data-bag data-bag
                  :msg-handler msg-handler
                      ;; TODO: Provide a proper token stream
                  :new-msgs [(make-msg-lex-done [])]}
    [{:kind :lex-done
      :token-stream token-stream}] {:running? true
                                    :data-bag (assoc data-bag
                                                     :token-stream token-stream)
                                    :msg-handler ast/msg-handler
                                    :new-msgs [(ast/make-msg-ast-parse token-stream)]}
    :else {:running? false
           :data-bag (assoc data-bag
                            :unknown-msg msg)
           :msg-handler msg-handler}))