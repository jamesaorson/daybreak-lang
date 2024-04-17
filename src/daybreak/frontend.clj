(ns daybreak.frontend
  (:require [babashka.fs :as fs]
            [clojure.core.match :refer [match]]
            [daybreak.lex :as lex]
            [net.clojars.jamesaorson.brain.core :as brain]))

(defn make-msg-source [source]
  {:kind :source
   :source source})

(defn make-msg-lexer-iter [lexer]
  {:kind :iter-lexer
   :lexer lexer})

(defn make-msg-lexer-done [lexer]
  {:kind :iter-lexer
   :lexer lexer})

(defn- msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [[:tick]] (let [{:keys [source]} data-bag]
                {:running? true
                 :data-bag data-bag
                 :msg-handler msg-handler
                 :new-msgs [(make-msg-source source)]})
    [{:kind :source
      :source source}] (let [lexer (lex/lex source)]
                         {:running? true
                          :data-bag data-bag
                          :msg-handler msg-handler
                          :new-msgs [(make-msg-lexer-iter lexer)]})
    [{:kind :lexer-iter
      :lexer lexer}] {:running? false
                      :data-bag data-bag
                      :msg-handler msg-handler
                      :new-msgs [(make-msg-lexer-done lexer)]}
    [{:kind :lexer-done
      :lexer _}] {:running? false
                  :data-bag data-bag
                  :msg-handler msg-handler}
    :else {:running? false
           :data-bag (assoc data-bag :unknown-msg msg)
           :msg-handler msg-handler}))

(defmulti run (fn [source] (type source)))
(defmethod run String [source]
  (brain/spark msg-handler {:source source}))
(defmethod run java.io.File [file]
  (run (slurp file)))
(defmethod run java.nio.file.Path [file]
  (run (fs/file file)))
