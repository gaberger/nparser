(ns ncp-parser.frr.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [ncp-parser.frr.core :refer :all]
            [ncp-parser.frr.spec :refer :all]
            [instaparse.core :as insta :refer [defparser]]))

; 
; (deftest frr-tests
;   (testing "Test FRR Parsing"
;     (is (not (nil?
;                (let [configuration (slurp (io/resource "configs/frr/router1.cfg"))
;                      _  (create-parser "frr")]
;                   (frr configuration))))))
;   (testing "Transformed Result"
;     (is (false?
;                (let [configuration (slurp (io/resource "configs/frr/router1.cfg"))
;                      _  (create-parser "frr")]
;                 (insta/failure? (bgp-transform (frr configuration)))))))
;   (testing "Transformed Spec"
;     (is (true?
;                (let [configuration (slurp (io/resource "configs/frr/router1.cfg"))
;                      _  (create-parser "frr")
;                      transform-map (bgp-transform (frr configuration))]
;                   (s/valid? :unq/device transform-map))))))
