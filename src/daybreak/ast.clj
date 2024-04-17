(ns daybreak.ast
  (:require [clojure.core.match :refer [match]]))

(def ^:private DEFAULT
  {:kind :ast
   :ast []})

(defn make-msg-ast-parse [token-stream]
  {:kind :ast-parse
   :token-stream token-stream})

(defn make-msg-ast-parse-done [ast]
  {:kind :ast-parse-done
   :ast ast})

(defn msg-handler [{:keys [data-bag msg msg-handler]}]
  (match [msg]
    [{:kind :ast-parse
      :token-stream _}] {:running? true
                         :data-bag data-bag
                         :msg-handler msg-handler
                         :new-msgs [(make-msg-ast-parse-done DEFAULT)]}
    [{:kind :ast-parse-done
      :ast ast}] {:running? false
                  :data-bag (assoc data-bag
                                   :ast ast)
                  :msg-handler msg-handler}
    :else {:running? false
           :data-bag (assoc data-bag
                            :unknown-msg msg)
           :msg-handler msg-handler}))
