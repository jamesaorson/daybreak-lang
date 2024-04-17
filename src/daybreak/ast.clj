(ns daybreak.ast
  (:require [clojure.core.match :refer [match]]))

(defmacro make-msg-ast-parse [token-stream]
  {:kind :ast-parse
   :token-stream token-stream})

(defmacro make-msg-ast-parse-done [ast]
  {:kind :ast-parse-done
   :ast ast})

(defmacro make-node [kind & {:keys [value children tags] :or {value nil children [] tags {}}}]
  {:kind kind
   :value value
   :children children
   :tags tags})

(def program "program")
(defmacro make-program [{:keys [children] :or {children []}}]
  (make-node program
             :value nil
             :children children))

(def fun-decl "fun-decl")
(defmacro make-fun-decl [name & {:keys [children] :or {children []}}]
  (make-node fun-decl
             :value name
             :children children))

(defmacro ^:private HELLO-WORLD []
  (make-program [(make-fun-decl "main")]))

(defn msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :ast-parse
      :token-stream _}] {:running? true
                         :data-bag data-bag
                         :msg-handler msg-handler
                         :new-msgs [(make-msg-ast-parse-done (HELLO-WORLD))]}
    [{:kind :ast-parse-done
      :ast ast}] {:running? false
                  :data-bag (assoc data-bag
                                   :ast ast)
                  :msg-handler msg-handler}
    :else {:running? false
           :data-bag (assoc data-bag
                            :unknown-msg msg)
           :msg-handler msg-handler}))
