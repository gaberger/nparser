(ns dev
  (:require
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [nparser.frr.parser :refer [create-frr-parser]]
    [clojure.pprint :refer [pprint]]
    [nparser.frr.generator :refer [generator] :as g]
    [nparser.utils :refer :all])) 

(def configurationv1 (get-file "./configs/frr/router1-test.cfg"))
(def grammarv1 (get-file "./parsers/frr/frr-new.ebnf"))
(def parserv1 (create-frr-parser grammarv1))
(require '[nparser.frr.transforms.v1.core :refer [transformer]])
; (def t1 (nparser.frr.transforms.v1.core/transformer (parserv1 configurationv1)))

;(g/generator t
;      newconf (str/join @g/container))


(def configurationv2 (get-file "./configs/frr/router1-test.cfg"))
(def grammarv2 (get-file "./parsers/frr/frr-new.ebnf"))
(def parserv2 (create-frr-parser grammarv2))
(def t2 (nparser.frr.transforms.v2.core/transformer (parserv2 configurationv2)))

(def t3 (nparser.frr.transforms.v3.core/transformer (parserv2 configurationv2)))
