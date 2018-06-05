(ns ncp-parser.frr.generator-test
  (:require  [clojure.test :refer :all]
             [clojure.java.io :as io]
             [clojure.spec.alpha :as s]
             [clojure.spec.gen.alpha :as sgen]
             [clojure.walk :refer [postwalk prewalk]]
             [clojure.string :as str]
             [ncp-parser.frr.generator :refer :all]))

;
(deftest generator_testing
  (testing "Test prewalk generator"
    (prewalk gen-config model)
    (is (= (str/join @container)
           "hostname  J\ninterface  eth0\nip address  10.0.18.1/24\ninterface  eth1\nip address  10.0.19.1/24\ninterface  eth2\nip address  10.0.15.2/24\ninterface  eth3\nip address  10.0.13.2/24\ninterface  eth4\nip address  10.0.11.2/24\ninterface  eth5\nip address  10.0.9.2/24\nrouter bgp  65525\nno synchronization \nbgp  router-id  192.0.0.1\nbgp  always-compare-med \nbgp  deterministic-med \nbgp  bestpath  compare-routerid \nbgp  bestpath  as-path confed \nbgp  confederation  identifier  100\nbgp  confederation  peers  65529 65528 65527 65530\n65529\n65528\n65527\n65530\nneighbor  10.0.18.2  remote-as  200\nneighbor  10.0.19.2  remote-as  300\nneighbor  10.0.15.1  remote-as  65527\nnext-hop-self  send-community  both\nadvertisement-interval  5\nneighbor  10.0.13.1  remote-as  65528\nnext-hop-self \nsend-community  both\nadvertisement-interval  5\nneighbor  10.0.18.2  <route-maps>  route-map  rm-in\nin\nroute-map  rm-export-2\nout\n"))))
