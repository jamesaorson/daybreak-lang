(ns daybreak.frontend
  (:require [babashka.fs :as fs]
            [clojure.core.match :refer [match]]
            [daybreak.lex :as lex]
            [net.clojars.jamesaorson.brain.core :as brain]))

(defmacro make-msg-load-source [source]
  {:kind :load-source
   :source source})

(defn- -msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :load-source
      :source source}] {:running? true
                        :data-bag (assoc data-bag
                                         :source source)
                        :msg-handler lex/msg-handler
                        :new-msgs [(lex/make-msg-lex (lex/lex source))]}
    :else {:running? false
           :data-bag (assoc data-bag
                            :unknown-msg msg)
           :msg-handler msg-handler}))

(defn- init-msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [[:tick]] (let [{:keys [source]} data-bag]
                {:running? true
                 :data-bag (assoc data-bag
                                  :source source)
                 :msg-handler -msg-handler
                 :new-msgs [(make-msg-load-source source)]})
    :else {:running? false
           :data-bag (assoc data-bag
                            :unknown-msg msg)
           :msg-handler msg-handler}))

(defmulti run (fn [source] (type source)))
(defmethod run String [source]
  (brain/spark init-msg-handler {:source source}))
(defmethod run java.io.File [file]
  (run (slurp file)))
(defmethod run java.nio.file.Path [file]
  (run (fs/file file)))
