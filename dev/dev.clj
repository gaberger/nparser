(ns dev
  (:require
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [nparser.frr.parser :refer [create-frr-parser]]
    [clojure.pprint :refer [pprint]]
    [nparser.frr.generator :refer [generator] :as g]
    [nparser.utils :refer :all]))

;
; (def configuration1 (get-file "./configs/frr/router1-test.cfg"))
; (def grammar1 (get-file "./parsers/frr/v1/frr-new.ebnf"))
; (def parser1 (create-frr-parser grammar1))
; (def t (nparser.frr.transforms.v1.core/transformer (parser1 configuration1)))

(def configuration (get-file "./configs/frr/frr.conf"))
(def grammar (get-file "./parsers/frr/v2/frr.ebnf"))
(def parser (create-frr-parser grammar))
(def t (nparser.frr.transforms.v2.core/transformer (parser configuration)))

      ; t (transformer (parser configuration))
