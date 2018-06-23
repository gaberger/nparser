(ns nparser.frr.parser-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [nparser.frr.parser :refer [create-frr-parser]]
            [nparser.frr.spec :refer :all]
            [nparser.frr.transforms.v1.core :refer [transformer]]
            [nparser.utils :as u]))


(deftest test-parser
  (let [configuration (u/get-file "./configs/frr/router1-test.cfg")
        grammar (u/get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))]


    (testing "Parse test configuration"
      (is (= [:device [:hostname "J"] [:interfaces [:interface [:name "eth0"] [:ip_address "10.0.18.1" "/" "24"]] [:interface [:name "eth1"] [:ip_address "10.0.19.1" "/" "24"]] [:interface [:name "eth2"] [:ip_address "10.0.15.2" "/" "24"]] [:interface [:name "eth3"] [:ip_address "10.0.13.2" "/" "24"]] [:interface [:name "eth4"] [:ip_address "10.0.11.2" "/" "24"]] [:interface [:name "eth5"] [:ip_address "10.0.9.2" "/" "24"]]] [:router_bgp [:asn "65525"] [:synchronization "no"] [:bgplist [:bgp [:router-id "192.0.0.1"]] [:bgp [:always-compare-med "always-compare-med"]] [:bgp [:deterministic-med "deterministic-med"]] [:bgp [:bestpath [:compare-routerid "compare-routerid"]]] [:bgp [:bestpath [:as-path_confed "as-path confed"]]] [:bgp [:confederation [:identifier "100"]]] [:bgp [:confederation [:peers "65527" "65528" "65529" "65530"]]]] [:neighbors [:neighbor [:naddr "10.0.18.2" [:remote-as "200"]]] [:neighbor [:naddr "10.0.19.2" [:remote-as "300"]]] [:neighbor [:naddr "10.0.15.1" [:remote-as "65527"]]] [:neighbor [:naddr "10.0.15.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.15.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.15.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.13.1" [:remote-as "65528"]]] [:neighbor [:naddr "10.0.13.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.13.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.13.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.11.1" [:remote-as "65529"]]] [:neighbor [:naddr "10.0.11.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.11.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.11.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.9.1" [:remote-as "65530"]]] [:neighbor [:naddr "10.0.9.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.9.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.9.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.18.2" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.18.2" [:route-map [:map "rm-export-2" "out"]]]] [:neighbor [:naddr "10.0.19.2" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.19.2" [:route-map [:map "rm-export-2" "out"]]]] [:neighbor [:naddr "10.0.15.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.15.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.13.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.13.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.11.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.11.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.9.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.9.1" [:route-map [:map "rm-export-1" "out"]]]]]]
              (parser configuration)])))

    (testing "Transformed config"
      (is (= {:<device> [{:hostname "J"} {:<interfaces> [{:interface {:<name> "eth0", :ip_address "10.0.18.1/24"}} {:interface {:<name> "eth1", :ip_address "10.0.19.1/24"}} {:interface {:<name> "eth2", :ip_address "10.0.15.2/24"}} {:interface {:<name> "eth3", :ip_address "10.0.13.2/24"}} {:interface {:<name> "eth4", :ip_address "10.0.11.2/24"}} {:interface {:<name> "eth5", :ip_address "10.0.9.2/24"}}]} {:router_bgp [{:<asn> 65525} {:+synchronization false} {:<bgplist> [{:bgp {:router-id "192.0.0.1"}} {:bgp {:+always-compare-med true}} {:bgp {:+deterministic-med true}} {:bgp {:bestpath {:+compare-routerid true}}} {:bgp {:bestpath {:+as-path_confed true}}} {:bgp {:confederation {:identifier 100}}} {:bgp {:confederation {:peers #{"65527" "65528" "65529" "65530"}}}}]} {:<neighbors> [{:neighbor {:10.0.18.2 {:remote-as 200}}} {:neighbor {:10.0.19.2 {:remote-as 300}}} {:neighbor {:10.0.15.1 {:remote-as 65527}}} {:neighbor {:10.0.15.1 {:+next-hop-self true}}} {:neighbor {:10.0.15.1 {:send-community "both"}}} {:neighbor {:10.0.15.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.13.1 {:remote-as 65528}}} {:neighbor {:10.0.13.1 {:+next-hop-self true}}} {:neighbor {:10.0.13.1 {:send-community "both"}}} {:neighbor {:10.0.13.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.11.1 {:remote-as 65529}}} {:neighbor {:10.0.11.1 {:+next-hop-self true}}} {:neighbor {:10.0.11.1 {:send-community "both"}}} {:neighbor {:10.0.11.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.9.1 {:remote-as 65530}}} {:neighbor {:10.0.9.1 {:+next-hop-self true}}} {:neighbor {:10.0.9.1 {:send-community "both"}}} {:neighbor {:10.0.9.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.18.2 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.18.2 {:route-map {:rm-export-2 "out"}}}} {:neighbor {:10.0.19.2 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.19.2 {:route-map {:rm-export-2 "out"}}}} {:neighbor {:10.0.15.1 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.15.1 {:route-map {:rm-export-1 "out"}}}} {:neighbor {:10.0.13.1 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.13.1 {:route-map {:rm-export-1 "out"}}}} {:neighbor {:10.0.11.1 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.11.1 {:route-map {:rm-export-1 "out"}}}} {:neighbor {:10.0.9.1 {:route-map {:rm-in "in"}}}} {:neighbor {:10.0.9.1 {:route-map {:rm-export-1 "out"}}}}]}]}]}
             t)))
    (testing "Test Specter Select"
      (is (= ["10.0.18.1/24" "10.0.19.1/24" "10.0.15.2/24" "10.0.13.2/24" "10.0.11.2/24" "10.0.9.2/24"]
             (select [:<device> ALL :<interfaces> ALL :interface :ip_address] t))))))

(deftest test-mutations
  (let [configuration (u/get-file "./configs/frr/router1-test.cfg")
        grammar (u/get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        modified (transform [:<device> ALL :<interfaces> ALL :interface (map-key :ip_address)] #(if (= :ip_address %) :prefix) t)
        query (select [:<device> ALL :<interfaces> ALL] modified)]
    (testing "Change keyword"
      (is (= [{:interface {:<name> "eth0", :prefix "10.0.18.1/24"}} {:interface {:<name> "eth1", :prefix "10.0.19.1/24"}} {:interface {:<name> "eth2", :prefix "10.0.15.2/24"}} {:interface {:<name> "eth3", :prefix "10.0.13.2/24"}} {:interface {:<name> "eth4", :prefix "10.0.11.2/24"}} {:interface {:<name> "eth5", :prefix "10.0.9.2/24"}}]
             query)))))
