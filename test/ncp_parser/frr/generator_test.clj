(ns ncp-parser.frr.generator-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [ncp-parser.frr.parser :refer [create-frr-parser]]
            [ncp-parser.frr.spec :refer :all]
            [ncp-parser.frr.transforms.v1.core :refer [transformer]]
            [ncp-parser.frr.generator :as g]
            [ncp-parser.utils :refer [get-github-file]]
            [clojure.string :as str]))


(deftest test-generator
  (let [configuration (get-github-file "master" {:path "frr/test/router1-test.cfg", :repo "network-config-store", :owner "gaberger"})
        grammar (get-github-file "master" {:path "parsers/frr/frr-new.ebnf", :repo "ncp_parser", :owner "gaberger"})
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        _ (g/generator t)
        conf-string (str/join @g/container)]
    (testing "Compare configs"
      (is (= configuration
             parser (create-frr-parser conf-string))))))
