;; Copyright © 2016-2018, JUXT LTD.
(ns dev
  (:require
   [clojure.core.async :as a :refer [>! <! >!! <!! chan buffer dropping-buffer sliding-buffer close! timeout alts! alts!! go-loop]]
   [clojure.java.io :as io]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.test :refer [run-all-tests]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [io.aviso.ansi]))


; (defn test-all []
;   (run-all-tests #"edge.*test$"))

(defn reset-and-test []
  (reset)
  (time (test-all)))

