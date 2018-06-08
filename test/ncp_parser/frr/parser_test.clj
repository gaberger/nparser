(ns ncp-parser.frr.parser-test
  (:require [clojure.test :refer :all]
            [com.rpl.specter :refer :all]
            [ncp-parser.frr.parser :refer [create-frr-parser frr]]
            [ncp-parser.frr.spec :refer :all]
            [ncp-parser.frr.transform :refer [frr-transform]]
            [ncp-parser.frr.utils :refer [get-github-file]]))


(deftest test-get-github-file
  (testing "get file")
  (is (=
        (get-github-file "master" {:path "configs/frr/router1.cfg", :repo "ncp_parser", :owner "gaberger"})
        "hostname J\n!\ninterface eth0\n ip address 10.0.18.1/24\n!\ninterface eth1\n ip address 10.0.19.1/24\n!\ninterface eth2\n ip address 10.0.15.2/24\n!\ninterface eth3\n ip address 10.0.13.2/24\n!\ninterface eth4\n ip address 10.0.11.2/24\n!\ninterface eth5\n ip address 10.0.9.2/24\n!\nrouter bgp 65525\n  no synchronization\n  bgp router-id 192.0.0.1\n  bgp always-compare-med\n  bgp deterministic-med\n  bgp bestpath compare-routerid\n  bgp bestpath as-path confed\n  bgp confederation identifier 100\n  bgp confederation peers 65527 65528 65529 65530\n  neighbor 10.0.18.2 remote-as 200\n  neighbor 10.0.19.2 remote-as 300\n  neighbor 10.0.15.1 remote-as 65527\n  neighbor 10.0.15.1 next-hop-self\n  neighbor 10.0.15.1 send-community both\n  neighbor 10.0.15.1 advertisement-interval 5\n  neighbor 10.0.13.1 remote-as 65528\n  neighbor 10.0.13.1 next-hop-self\n  neighbor 10.0.13.1 send-community both\n  neighbor 10.0.13.1 advertisement-interval 5\n  neighbor 10.0.11.1 remote-as 65529\n  neighbor 10.0.11.1 next-hop-self\n  neighbor 10.0.11.1 send-community both\n  neighbor 10.0.11.1 advertisement-interval 5\n  neighbor 10.0.9.1 remote-as 65530\n  neighbor 10.0.9.1 next-hop-self\n  neighbor 10.0.9.1 send-community both\n  neighbor 10.0.9.1 advertisement-interval 5\n  neighbor 10.0.18.2 route-map rm-in in\n  neighbor 10.0.18.2 route-map rm-export-2 out\n  neighbor 10.0.19.2 route-map rm-in in\n  neighbor 10.0.19.2 route-map rm-export-2 out\n  neighbor 10.0.15.1 route-map rm-in in\n  neighbor 10.0.15.1 route-map rm-export-1 out\n  neighbor 10.0.13.1 route-map rm-in in\n  neighbor 10.0.13.1 route-map rm-export-1 out\n  neighbor 10.0.11.1 route-map rm-in in\n  neighbor 10.0.11.1 route-map rm-export-1 out\n  neighbor 10.0.9.1 route-map rm-in in\n  neighbor 10.0.9.1 route-map rm-export-1 out\n!\nip prefix-list pl-1 permit 1.0.0.0/24\nip prefix-list pl-2 deny 1.0.0.0/24\nip prefix-list pl-3 permit 1.0.1.0/24\nip prefix-list pl-4 deny 1.0.1.0/24\nip prefix-list pl-5 permit 2.0.0.0/24\nip prefix-list pl-6 deny 2.0.0.0/24\nip prefix-list pl-7 permit 2.0.1.0/24\nip prefix-list pl-8 deny 2.0.1.0/24\nip prefix-list pl-9 permit 0.0.0.0/0 le 32\nip prefix-list pl-10 deny 0.0.0.0/0 le 32\n!\nip community-list standard cl-1 permit 200:1\nip community-list standard cl-2 permit 200:2\nip community-list standard cl-3 permit 100:1\nip community-list standard cl-4 permit 100:2\nip community-list standard cl-5 permit 100:3\nip community-list standard cl-6 permit 100:4\nip community-list standard LOCAL permit 100:1 100:2 100:3 100:4\n!\nip as-path access-list path-1 permit ^\\(?(65527|65528|65529|65530)_\nip as-path access-list path-3 permit ^\\(?300_\nip as-path access-list path-4 permit ^\\(?200_\n!\nroute-map rm-in permit 10\n  match ip address prefix-list pl-1\n  match as-path path-1\n  set community 100:1 additive\n!\nroute-map rm-in permit 20\n  match ip address prefix-list pl-2\n!\nroute-map rm-in permit 30\n  match ip address prefix-list pl-3\n  match as-path path-1\n  set community 100:1 additive\n!\nroute-map rm-in permit 40\n  match ip address prefix-list pl-4\n!\nroute-map rm-in permit 50\n  match ip address prefix-list pl-5\n  match as-path path-1\n  set community 100:2 additive\n!\nroute-map rm-in permit 60\n  match ip address prefix-list pl-6\n!\nroute-map rm-in permit 70\n  match ip address prefix-list pl-7\n  match as-path path-1\n  set community 100:2 additive\n!\nroute-map rm-in permit 80\n  match ip address prefix-list pl-8\n!\nroute-map rm-in permit 90\n  match ip address prefix-list pl-9\n  match as-path path-3\n  set community 100:3 additive\n!\nroute-map rm-in permit 100\n  match community cl-1\n  match ip address prefix-list pl-9\n  match as-path path-1\n  set community 100:1 additive\n!\nroute-map rm-in permit 110\n  match ip address prefix-list pl-9\n  match as-path path-4\n  set local-preference 99\n  set community 100:4 additive\n!\nroute-map rm-in permit 120\n  match community cl-2\n  match ip address prefix-list pl-9\n  match as-path path-1\n  set local-preference 99\n  set community 100:1 additive\n!\nroute-map rm-in permit 130\n  match ip address prefix-list pl-10\n!\nroute-map rm-export-1 permit 10\n  match community cl-3\n!\nroute-map rm-export-1 permit 20\n  match community cl-4\n!\nroute-map rm-export-1 permit 30\n  match community cl-5\n  set community 200:1 additive\n!\nroute-map rm-export-1 permit 40\n  match community cl-6\n  set community 200:2 additive\n!\nroute-map rm-export-2 permit 10\n  match community cl-4\n!\n")))

(deftest test-frr-parsing
  (testing "Test FRR Parsing"
    (is (not (nil?
               (let [configuration  (get-github-file "master" {:path "configs/frr/router1.cfg", :repo "ncp_parser", :owner "gaberger"})
                     _  (create-frr-parser (get-github-file "master" {:path "parsers/frr/frr.ebnf", :repo "ncp_parser", :owner "gaberger"}))]
                 (frr configuration)))))))

(deftest specter-query
  (testing "Test Specter Select"
    (is (=
          (let [configuration  (get-github-file "master" {:path "configs/frr/router1.cfg", :repo "ncp_parser", :owner "gaberger"})
                _  (create-frr-parser (get-github-file "master" {:path "parsers/frr/frr.ebnf", :repo "ncp_parser", :owner "gaberger"}))
                m  (frr-transform (frr configuration))]
            (select [:configuration :interfaces ALL :ip-cidr] m))
          ["10.0.18.1/24" "10.0.19.1/24" "10.0.15.2/24" "10.0.13.2/24" "10.0.11.2/24" "10.0.9.2/24"])))
  (testing "Change keyword"
    (is (=
          (let [configuration  (get-github-file "master" {:path "configs/frr/router1.cfg", :repo "ncp_parser", :owner "gaberger"})
                _              (create-frr-parser (get-github-file "master" {:path "parsers/frr/frr.ebnf", :repo "ncp_parser", :owner "gaberger"}))
                m              (frr-transform (frr configuration))
                modified       (transform [:configuration :interfaces ALL (map-key :ip-cidr)] #(if (= :ip-cidr %) :prefix) m)]
            (select [:configuration :interfaces ALL] modified))
          [{:name "eth0", :prefix "10.0.18.1/24"} {:name "eth1", :prefix "10.0.19.1/24"} {:name "eth2", :prefix "10.0.15.2/24"} {:name "eth3", :prefix "10.0.13.2/24"} {:name "eth4", :prefix "10.0.11.2/24"} {:name "eth5", :prefix "10.0.9.2/24"}]))))
