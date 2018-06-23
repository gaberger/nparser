(ns dev
  (:require
    [clojure.walk :refer [postwalk prewalk]]
    [clojure.string :as str]
    [clojure.pprint :refer [pprint]]
    [clojure.reflect :refer [reflect]]
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.test :refer [run-all-tests]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]))

;
; (def configuration (get-github-file "master" {:path "frr/test/router1-test.cfg", :repo "network-config-store", :owner "gaberger"}))
; (def grammar (get-github-file "master" {:path "parsers/frr/frr-new.ebnf", :repo "ncp_parser", :owner "gaberger"}))
; (def parser (create-frr-parser grammar))
; (def t (transformer (parser configuration)))


;(g/generator t
;      newconf (str/join @g/container))
