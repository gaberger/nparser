(ns dev
  (:require
    [clojure.walk :refer [postwalk prewalk]]
    [clojure.string :as str]
    [clojure.pprint :refer [pprint]]
    [clojure.reflect :refer [reflect]]
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.test :refer [run-all-tests]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [nparser.frr.parser :refer [create-frr-parser]]
    [nparser.frr.transforms.v2.core :refer [transformer]]
    [nparser.frr.generator :refer [generator] :as g]
    [nparser.utils :refer :all])) 

(def configuration (get-file "./configs/frr/router1-test.cfg"))
(def grammar (get-file "./parsers/frr/frr-new.ebnf"))
(def parser (create-frr-parser grammar))

;(g/generator t
;      newconf (str/join @g/container))
