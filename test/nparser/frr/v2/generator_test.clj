(ns nparser.frr.v2.generator-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [nparser.parser :refer [create-parser]]
            [nparser.frr.specs.v1.spec :refer :all]
            [nparser.frr.transforms.v2.core :refer [transformer]]
            [nparser.generator :as g]
            [nparser.utils :refer :all]
            ; [yaml.core :as yaml]
            [clojure.string :as str]))


(deftest test-generator
  (let [configuration (get-file "./configs/frr/topo-evpn/frr.conf")
        grammar (get-file "./parsers/frr/v2/frr.ebnf")
        parser (create-parser grammar)
        t (transformer (parser configuration))
        newconf (g/generator t)
        _ (reset! g/container [])]
    (testing "Compare configs"
      (is (= (parser configuration)
             (parser newconf))))))

; (deftest test-yaml-importer-export
;   (let [configuration (get-file "./configs/frr/router1-test.cfg")
;         grammar (get-file "./parsers/frr/frr-new.ebnf")
;         parser (create-frr-parser grammar)
;         t (transformer (parser configuration))
;         ey (edn->yaml t)
;         ye (yaml->edn ey)]
;     (testing "Compare configs"
;       (is (= t
;              ye)))))
