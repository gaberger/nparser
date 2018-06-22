(ns ncp-parser.frr.generator-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [ncp-parser.frr.parser :refer [create-frr-parser]]
            [ncp-parser.frr.spec :refer :all]
            [ncp-parser.frr.transforms.v1.core :refer [transformer]]
            [ncp-parser.frr.generator :as g]
            [ncp-parser.utils :refer :all]
            [yaml.core :as yaml]
            [clojure.string :as str]))


(deftest test-generator
  (let [configuration (get-file "./configs/frr/router1-test.cfg")
        grammar (get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        _ (g/generator t)
        newconf (str/join @g/container)
        _ (reset! g/container [])]
    (testing "Compare configs"
      (is (= (parser configuration)
             (parser newconf))))))

(deftest test-yaml-importer-export
  (let [configuration (get-file "./configs/frr/router1-test.cfg")
        grammar (get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        ey (edn->yaml t)
        ye (yaml->edn ey)]
    (testing "Compare configs"
      (is (= t
             ye)))))