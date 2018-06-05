(ns dev
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.test :refer [run-all-tests]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]))

(in-ns `ncp-parser.frr.generator)

; (defn test-all []
;   (run-all-tests #"edge.*test$"))
