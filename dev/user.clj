(ns user
  (:require
   [clojure.tools.namespace.repl :refer :all]
   [io.aviso.ansi]
   [spyscope.core]))

(defn dev
  "Call this to launch the dev system"
  []
  (println "[ncp-parser] Loading Clojure code, please wait...")
  (require 'dev)
  (in-ns 'dev))

(defn fixed!
  "If, for some reason, the Clojure code in the project fails to
  compile - we still bring up a REPL to help debug the problem. Once
  the problem has been resolved you can call this function to continue
  development."
  []
  (refresh-all)
  (in-ns 'dev))
