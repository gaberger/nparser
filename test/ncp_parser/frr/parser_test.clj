(ns ncp-parser.frr.parser-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer [ALL transform map-key select]]
            [ncp-parser.frr.parser :refer [create-frr-parser]]
            [ncp-parser.frr.spec :refer :all]
            [ncp-parser.frr.transforms.v1.core :refer [transformer]]
            [ncp-parser.utils :refer [get-github-file]]))


(deftest test-get-github-file
  (let [configuration (get-github-file "master" {:path "frr/test/router1-test.cfg", :repo "network-config-store", :owner "gaberger"})
        grammar (get-github-file "master" {:path "parsers/frr/frr-new.ebnf", :repo "ncp_parser", :owner "gaberger"})
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))]


    (testing "Parse test configuration"
      (is (= [:device [:hostname "J"] [:interfaces [:interface [:name "eth0"] [:ip_address "10.0.18.1" "/" "24"]] [:interface [:name "eth1"] [:ip_address "10.0.19.1" "/" "24"]] [:interface [:name "eth2"] [:ip_address "10.0.15.2" "/" "24"]] [:interface [:name "eth3"] [:ip_address "10.0.13.2" "/" "24"]] [:interface [:name "eth4"] [:ip_address "10.0.11.2" "/" "24"]] [:interface [:name "eth5"] [:ip_address "10.0.9.2" "/" "24"]]] [:router_bgp [:asn "65525"] [:synchronization "no"] [:bgplist [:bgp [:router-id "192.0.0.1"]] [:bgp [:always-compare-med "always-compare-med"]] [:bgp [:deterministic-med "deterministic-med"]] [:bgp [:bestpath [:compare-routerid "compare-routerid"]]] [:bgp [:bestpath [:as-path_confed "as-path confed"]]] [:bgp [:confederation [:identifier "100"]]] [:bgp [:confederation [:peers "65527" "65528" "65529" "65530"]]]] [:neighbors [:neighbor [:naddr "10.0.18.2" [:remote-as "200"]]] [:neighbor [:naddr "10.0.19.2" [:remote-as "300"]]] [:neighbor [:naddr "10.0.15.1" [:remote-as "65527"]]] [:neighbor [:naddr "10.0.15.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.15.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.15.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.13.1" [:remote-as "65528"]]] [:neighbor [:naddr "10.0.13.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.13.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.13.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.11.1" [:remote-as "65529"]]] [:neighbor [:naddr "10.0.11.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.11.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.11.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.9.1" [:remote-as "65530"]]] [:neighbor [:naddr "10.0.9.1" [:next-hop-self]]] [:neighbor [:naddr "10.0.9.1" [:send-community "both"]]] [:neighbor [:naddr "10.0.9.1" [:advertisement-interval "5"]]] [:neighbor [:naddr "10.0.18.2" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.18.2" [:route-map [:map "rm-export-2" "out"]]]] [:neighbor [:naddr "10.0.19.2" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.19.2" [:route-map [:map "rm-export-2" "out"]]]] [:neighbor [:naddr "10.0.15.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.15.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.13.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.13.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.11.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.11.1" [:route-map [:map "rm-export-1" "out"]]]] [:neighbor [:naddr "10.0.9.1" [:route-map [:map "rm-in" "in"]]]] [:neighbor [:naddr "10.0.9.1" [:route-map [:map "rm-export-1" "out"]]]]]]
              (parser configuration)])))

    (testing "Transformed config"
      (is (= {:<device> [{:hostname "J"} {:<interfaces> [{:interface {:<name> "eth0", :ip_address "10.0.18.1/24"}} {:interface {:<name> "eth1", :ip_address "10.0.19.1/24"}} {:interface {:<name> "eth2", :ip_address "10.0.15.2/24"}} {:interface {:<name> "eth3", :ip_address "10.0.13.2/24"}} {:interface {:<name> "eth4", :ip_address "10.0.11.2/24"}} {:interface {:<name> "eth5", :ip_address "10.0.9.2/24"}}]} {:router_bgp [{:<asn> 65525} {:+synchronization false} {:<bgplist> [{:bgp {:router-id "192.0.0.1"}} {:bgp {:+always-compare-med true}} {:bgp {:+deterministic-med true}} {:bgp {:bestpath {:+compare-routerid true}}} {:bgp {:bestpath {:+as-path_confed true}}} {:bgp {:confederation [:identifier "100"]}} {:bgp {:confederation [:peers #{65529 65528 65527 65530}]}}]} {:<neighbors> [{:neighbor {:10.0.18.2 {:remote-as "200"}}} {:neighbor {:10.0.19.2 {:remote-as "300"}}} {:neighbor {:10.0.15.1 {:remote-as "65527"}}} {:neighbor {:10.0.15.1 {:+next-hop-self true}}} {:neighbor {:10.0.15.1 {:send-community "both"}}} {:neighbor {:10.0.15.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.13.1 {:remote-as "65528"}}} {:neighbor {:10.0.13.1 {:+next-hop-self true}}} {:neighbor {:10.0.13.1 {:send-community "both"}}} {:neighbor {:10.0.13.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.11.1 {:remote-as "65529"}}} {:neighbor {:10.0.11.1 {:+next-hop-self true}}} {:neighbor {:10.0.11.1 {:send-community "both"}}} {:neighbor {:10.0.11.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.9.1 {:remote-as "65530"}}} {:neighbor {:10.0.9.1 {:+next-hop-self true}}} {:neighbor {:10.0.9.1 {:send-community "both"}}} {:neighbor {:10.0.9.1 {:advertisement-interval "5"}}} {:neighbor {:10.0.18.2 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.18.2 {:routemap {:rm-export-2 "out"}}}} {:neighbor {:10.0.19.2 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.19.2 {:routemap {:rm-export-2 "out"}}}} {:neighbor {:10.0.15.1 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.15.1 {:routemap {:rm-export-1 "out"}}}} {:neighbor {:10.0.13.1 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.13.1 {:routemap {:rm-export-1 "out"}}}} {:neighbor {:10.0.11.1 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.11.1 {:routemap {:rm-export-1 "out"}}}} {:neighbor {:10.0.9.1 {:routemap {:rm-in "in"}}}} {:neighbor {:10.0.9.1 {:routemap {:rm-export-1 "out"}}}}]}]}]}
             t)))
    (testing "Test Specter Select"
      (is (= ["10.0.18.1/24" "10.0.19.1/24" "10.0.15.2/24" "10.0.13.2/24" "10.0.11.2/24" "10.0.9.2/24"]
             (select [:<device> ALL :<interfaces> ALL :interface :ip_address] t))))))

(deftest test-mutations
  (let [configuration (get-github-file "master" {:path "frr/test/router1-test.cfg", :repo "network-config-store", :owner "gaberger"})
        grammar (get-github-file "master" {:path "parsers/frr/frr-new.ebnf", :repo "ncp_parser", :owner "gaberger"})
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        modified (transform [:<device> ALL :<interfaces> ALL :interface (map-key :ip_address)] #(if (= :ip_address %) :prefix) t)
        query (select [:<device> ALL :<interfaces> ALL] modified)]
    (testing "Change keyword"
      (is (= [{:interface {:<name> "eth0", :prefix "10.0.18.1/24"}} {:interface {:<name> "eth1", :prefix "10.0.19.1/24"}} {:interface {:<name> "eth2", :prefix "10.0.15.2/24"}} {:interface {:<name> "eth3", :prefix "10.0.13.2/24"}} {:interface {:<name> "eth4", :prefix "10.0.11.2/24"}} {:interface {:<name> "eth5", :prefix "10.0.9.2/24"}}]
             query)))))
