(ns nparser.frr.v2.parser-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL map-key select transform] :as sp]
            [nparser.frr.parser :refer [create-frr-parser]]
            [nparser.frr.spec :refer :all]
            [nparser.frr.transforms.v2.core :refer [transformer]]
            [nparser.utils :as u]))


(deftest test-parser
  (let [configuration (u/get-file "./configs/frr/topo-evpn/frr.conf")
        grammar (u/get-file "./parsers/frr/v2/frr.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))]


    (testing "Parse test configuration"
      (is (= [:device [:service "integrated-vtysh-config"] [:interfaces [:interface [:name "GigEthernet0/0/0"] [:description "Faces" "Leaf" "switch" "1"] [:ip_address "10.1.1.1" "/" "24"]] [:interface [:name "GigEthernet0/0/1"] [:description "Faces" "Spine" "switch" "1"] [:ip_address "10.1.2.1" "/" "24"]] [:interface [:name "None0"] [:description "For" "blackholing" "traffic"]] [:interface [:name "loop0"] [:ip_address "10.10.1.1" "/" "32"]]] [:router-id "10.10.1.1"] [:router_bgp [:asn "65000"] [:neighbors [:neighbor [:npeer "LEAF" "peer-group"]] [:neighbor [:npeer "RR" "peer-group"]] [:neighbor [:npeer "TEST" "peer-group"]] [:neighbor [:npeer "UNDEFINED" "peer-group"]] [:neighbor [:naddr "10.1.1.2" [:remote-as "64001"]]] [:neighbor [:naddr "10.1.1.2" [:peer-group "LEAF"]]] [:neighbor [:naddr "10.1.2.2" [:remote-as "73003"]]] [:neighbor [:naddr "10.1.2.2" [:peer-group "UNDEFINED"]]] [:neighbor [:naddr "10.1.2.2" [:update-source "10.1.2.1"]]]] [:afiu [:address-family "ipv4 unicast"] [:afneighbors [:afneighbor [:afnpeer "LEAF" "addpath-tx-all-paths"]] [:afneighbor [:afnpeer "LEAF" "soft-reconfiguration inbound"]] [:afneighbor [:afnpeer "RR" "soft-reconfiguration inbound"]]] [:exit-address-family "exit-address-family"] [:address-family "ipv6 unicast"] [:afneighbors [:afneighbor [:afnpeer "LEAF" "soft-reconfiguration inbound"]]] [:afneighbors [:afneighbor [:afnpeer "TEST" "soft-reconfiguration inbound"]]] [:exit-address-family "exit-address-family"]]] [:vnc [:vncdefaults "defaults"] [:response-lifetime "3600"] [:exitvnc "exit-vnc"]] [:line "vty"]]
             (parser configuration))))

    (testing "Transformed config"
      (is (= {:<device> [{:service "integrated-vtysh-config"} {:<interfaces> [{:interface {:<name> "GigEthernet0/0/0", :description "\"Faces Leaf switch 1\"", :ip_address "10.1.1.1/24"}} {:interface {:<name> "GigEthernet0/0/1", :description "\"Faces Spine switch 1\"", :ip_address "10.1.2.1/24"}} {:interface {:<name> "None0", :description "\"For blackholing traffic\""}} {:interface {:<name> "loop0", :ip_address "10.10.1.1/32"}}]} {:router-id "10.10.1.1"} {:router_bgp [{:<asn> 65000} {:<neighbors> [{:neighbor {:LEAF "peer-group"}} {:neighbor {:RR "peer-group"}} {:neighbor {:TEST "peer-group"}} {:neighbor {:UNDEFINED "peer-group"}} {:neighbor {:10.1.1.2 {:remote-as 64001}}} {:neighbor {:10.1.1.2 [:peer-group "LEAF"]}} {:neighbor {:10.1.2.2 {:remote-as 73003}}} {:neighbor {:10.1.2.2 [:peer-group "UNDEFINED"]}} {:neighbor {:10.1.2.2 [:update-source "10.1.2.1"]}}]} {:<afiu> [{:address-family "ipv4 unicast"} {:<afneighbors> [{:neighbor {:LEAF "addpath-tx-all-paths"}} {:neighbor {:LEAF "soft-reconfiguration inbound"}} {:neighbor {:RR "soft-reconfiguration inbound"}}]} {:<exit-address-family> "exit-address-family"} {:address-family "ipv6 unicast"} {:<afneighbors> [{:neighbor {:LEAF "soft-reconfiguration inbound"}}]} {:<afneighbors> [{:neighbor {:TEST "soft-reconfiguration inbound"}}]} {:<exit-address-family> "exit-address-family"}]}]} {:vnc [{:<vncdefaults> "defaults"} [:response-lifetime "3600"] {:<exitvnc> "exit-vnc"}]} {:line "vty"}]}
             t))) 
    (testing "Test Specter Select"
      (is (= ["10.1.1.1/24" "10.1.2.1/24" nil "10.10.1.1/32"]
             (select [:<device> ALL :<interfaces> ALL :interface :ip_address] t))))

; (deftest test-mutations
;   (let [configuration (u/get-file "./configs/frr/router1-test.cfg")
;         grammar (u/get-file "./parsers/frr/frr-new.ebnf")
;         parser (create-frr-parser grammar)
;         t (transformer (parser configuration))
;         modified (transform [:<device> ALL :<interfaces> ALL :interface (map-key :ip_address)] #(if (= :ip_address %) :prefix) t)
;         query (select [:<device> ALL :<interfaces> ALL] modified)]
    (testing "Change keyword"
      (let [modified (transform [:<device> ALL :<interfaces> ALL :interface (map-key :ip_address)] #(if (= :ip_address %) :prefix) t)
            query (select [:<device> ALL :<interfaces> ALL] modified)]
        (is (= [{:interface {:<name> "GigEthernet0/0/0", :description "\"Faces Leaf switch 1\"", :prefix "10.1.1.1/24"}} {:interface {:<name> "GigEthernet0/0/1", :description "\"Faces Spine switch 1\"", :prefix "10.1.2.1/24"}} {:interface {:<name> "None0", :description "\"For blackholing traffic\""}} {:interface {:<name> "loop0", :prefix "10.10.1.1/32"}}]  
               query))))))
