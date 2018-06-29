


```
[:device
 [:hostname "foobar"]
 [:service "integrated-vtysh-config"]
 [:interfaces
  [:interface
   [:name "GigEthernet0/0/0"]
   [:description "Faces" "Leaf" "switch" "1"]
   [:ip_address "10.1.1.1" "/" "24"]]
  [:interface
   [:name "GigEthernet0/0/1"]
   [:description "Faces" "Spine" "switch" "1"]
   [:ip_address "10.1.2.1" "/" "24"]]
  [:interface
   [:name "None0"]
   [:description "For" "blackholing" "traffic"]]
  [:interface [:name "loop0"] 
   [:ip_address "10.10.1.1" "/" "32"]]]
 [:router-id "10.10.1.1"]
 [:router_bgp
  [:asn "65000"]
  [:neighbors
   [:neighbor [:npeer "LEAF" "peer-group"]]
   [:neighbor [:npeer "RR" "peer-group"]]
   [:neighbor [:npeer "TEST" "peer-group"]]
   [:neighbor [:npeer "UNDEFINED" "peer-group"]]
   [:neighbor [:naddr "10.1.1.2" [:remote-as "64001"]]]
   [:neighbor [:naddr "10.1.1.2" [:peer-group "LEAF"]]]
   [:neighbor [:naddr "10.1.2.2" [:remote-as "73003"]]]
   [:neighbor [:naddr "10.1.2.2" [:peer-group "UNDEFINED"]]]
   [:neighbor [:naddr "10.1.2.2" [:update-source "10.1.2.1"]]]]
  [:afiu
   [:address-family "ipv4 unicast"]
   [:afneighbors
    [:afneighbor [:afnpeer "LEAF" "addpath-tx-all-paths"]]
    [:afneighbor [:afnpeer "LEAF" "soft-reconfiguration inbound"]]
    [:afneighbor [:afnpeer "RR" "soft-reconfiguration inbound"]]]
   [:exit-address-family "exit-address-family"]
   [:address-family "ipv6 unicast"]
   [:afneighbors
    [:afneighbor [:afnpeer "LEAF" "soft-reconfiguration inbound"]]]
   [:afneighbors
    [:afneighbor [:afnpeer "TEST" "soft-reconfiguration inbound"]]]
   [:exit-address-family "exit-address-family"]]]
 [:vnc
  [:vncdefaults "defaults"]
  [:response-lifetime "3600"]
  [:exitvnc "exit-vnc"]]
 [:line "vty"]]
 ```