(ns daybreak.frontend
  (:require [babashka.fs :as fs]
            [clojure.core.match :refer [match]]
            [daybreak.ast :as ast]
            [daybreak.lex :as lex]
            [net.clojars.jamesaorson.brain.core :as brain]))

(defn make-msg-ast-parse [token-stream]
  {:kind :ast-parse
   :token-stream token-stream})

(defn make-msg-ast-parse-done [ast]
  {:kind :ast-parse-done
   :ast ast})

(defn make-msg-source [source]
  {:kind :source
   :source source})

(defn make-msg-lex [lexer]
  {:kind :lex
   :lexer lexer})

(defn make-msg-lex-done [token-stream]
  {:kind :lex-done
   :token-stream token-stream})

(defn- ast-msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :ast-parse
      :ast ast}] {:running? true
                  :data-bag (assoc data-bag :ast ast)
                  :msg-handler msg-handler
                  :new-msgs [(make-msg-ast-parse-done ast)]}
    [{:kind :ast-parse-done
      :ast ast}] {:running? false
                  :data-bag (assoc data-bag :ast ast)
                  :msg-handler msg-handler}
    :else {:running? false
           :data-bag (assoc data-bag :unknown-msg msg)
           :msg-handler msg-handler}))

(defn- lex-msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :lex
      :lexer _}] {:running? true
                  :data-bag data-bag
                  :msg-handler msg-handler
                      ;; TODO: Provide a proper token stream
                  :new-msgs [(make-msg-lex-done [{}])]}
    [{:kind :lex-done
      :token-stream token-stream}] {:running? true
                                    :data-bag data-bag
                                    :msg-handler ast-msg-handler
                                    :new-msgs [(make-msg-ast-parse token-stream)]}
    :else {:running? false
           :data-bag (assoc data-bag :unknown-msg msg)
           :msg-handler msg-handler}))

(defn- source-msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :source
      :source source}] {:running? true
                        :data-bag data-bag
                        :msg-handler lex-msg-handler
                        :new-msgs [(make-msg-lex (lex/lex source))]}
    :else {:running? false
           :data-bag (assoc data-bag :unknown-msg msg)
           :msg-handler msg-handler}))

(defn- init-msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [[:tick]] (let [{:keys [source]} data-bag]
                {:running? true
                 :data-bag data-bag
                 :msg-handler source-msg-handler
                 :new-msgs [(make-msg-source source)]})
    :else {:running? false
           :data-bag (assoc data-bag :unknown-msg msg)
           :msg-handler msg-handler}))

(defmulti run (fn [source] (type source)))
(defmethod run String [source]
  (brain/spark init-msg-handler {:source source}))
(defmethod run java.io.File [file]
  (run (slurp file)))
(defmethod run java.nio.file.Path [file]
  (run (fs/file file)))
