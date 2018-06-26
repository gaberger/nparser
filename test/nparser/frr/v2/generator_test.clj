(ns nparser.frr.v2.generator-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [nparser.frr.parser :refer [create-frr-parser]]
            [nparser.frr.spec :refer :all]
            [nparser.frr.transforms.v2.core :refer [transformer]]
            [nparser.frr.generator :as g]
            [nparser.utils :refer :all]
            [yaml.core :as yaml]
            [clojure.string :as str]))


(deftest test-generator
  (let [configuration (get-file "./configs/frr/router1-test.cfg")
        grammar (get-file "./parsers/frr/v1/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        _ (g/generator t)
        newconf (str/join @g/container)
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
