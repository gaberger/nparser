## Network Configuration Parser
This is an example of a Network Configuration Parser written in Clojure and using the Instaparse library

### Dependencies
Install Clojure and command line tools ( see [Getting Started](https://clojure.org/guides/getting_started) )

## Run program
```
At the moment this is running tests from ncp-parser.core-test for validation soon I will
add commmand line arguments to process configurations and a service endpoint
```


`clj -m ncp-parser.core`


## Sample Configuration

```
router bgp 65525
    no synchronization
    bgp router-id 192.0.0.1
    bgp always-compare-med
    bgp deterministic-med
    bgp bestpath compare-routerid
    bgp bestpath as-path confed
    bgp confederation identifier 100
    bgp confederation peers 65527 65528 65529 65530"
```

## EBNF Parser Function

Create a parser based on grammar stored at `resources/parsers`

```(create-parser "frr")```

```Then you can use the parser against configurations such as:```

```(frr (slurp (io/resource "configs/router1.cfg")))```

#### Example grammar

~~~BNF
(*This is the EBNF Grammar for FRR router*)


device = hostname interface-list router ip-prefix-list ip-community-list ip-as-path-list route-map-list
(* ip-prefix-list ip-community-list *)

hostname = <'hostname'> word

(* interface section *)

interface-list = interface*
interface = (interface-name ip-cidr)*

interface-name = <'interface'> word
ip-cidr = <'ip address'> cidr


(* router section *)
router = bgp-global bgp-med bgp-bestpath bgp-confederation neighbor-list

bgp-global =  (otherkeys | asn | router-id)*
asn =  &'router bgp' <'router bgp'> number
otherkeys = 'no synchronization'

bgp-med = (<'bgp'> bgp)*

<bgp> = 'always-compare-med' | 'deterministic-med'

router-id = (<'bgp router-id'> address)

bgp-bestpath = &'bgp bestpath' (<'bgp bestpath'> best-path)*
<best-path> = ('as-path confed' | 'as-path multipath-relax' | 'compare-routerid')

bgp-confederation = &'bgp confederation' (<'bgp confederation'> confederation)*
<confederation> = (confederation-identifier | confederation-peers)
confederation-identifier =  <'identifier'> number+
confederation-peers = <'peers'> number+

neighbor-list = (neighbor-term neighbor)*
neighbor =  neighbor-address (remote-as | neighbor-options | send-community | advertisement-interval | neighbor-route-map)*

remote-as = <'remote-as'> number
neighbor-options = next-hop-self | update-source
<send-community> = <'send-community'> send-community-options
neighbor-address = address
<neighbor-term> = <'neighbor'>

advertisement-interval = <'advertisement-interval'> number
<next-hop-self> = 'next-hop-self' ?['all']
<update-source> = 'update-source' (if-name | address)
if-name = word
send-community-options = send-community-both
<send-community-both> = 'both'

neighbor-route-map = route-map direction
route-map = <'route-map'> rm
direction = 'in' | 'out'
<rm> = word

(* ip prefix list *)
(* TODO more work here *)
ip-prefix-list = (<'ip prefix-list'>  ip-prefix-record)*
ip-prefix-record = prefix-list-name prefix-perm ip-prefix ?[range]
prefix-list-name = word
ip-prefix = prefix
prefix-perm = permit | deny
<range> = (le  | gt)
le = <'le'> number
gt = <'gt'> number

(* ip community list *)
ip-community-list = (<'ip community-list'> ip-community-options)*
<ip-community-options> = ip-community-list-standard | ip-community-list-expanded

ip-community-list-standard = <'standard'> community-list-name community-perm community+
ip-community-list-expanded = <'expanded'> community-list-name community-perm community+




(* ip as-path *)
ip-as-path-list = (<'ip as-path'> access-list-record)*
access-list-record = <'access-list'> path as-path-perm as-path-regex
path = word
as-path-perm = permit | deny
as-path-regex = word

(* route map *)
route-map-list = (<'route-map'> route-map-record)*
route-map-record = route-map-name route-map-perm order match-list* ?[route-map-set-list*]

route-map-name = word
route-map-perm = permit | deny
order = number

(* route match list *)
<match-list> = match-list1 | match-list2 | match-list3
<match-list1> = match-ip-address-access-list | match-ip-address-prefix-list | match-ip-address-prefix-len
<match-list2> = match-ip-next-hop | match-as-path  | match-metric | match-tag | match-local-preference
<match-list3> = match-community | match-peer-interface-name | match-source-protocol | match-source-instance

(* route match terminators *)
match-ip-address-access-list = <'match ip address access-list'>  number
match-ip-address-prefix-list = <'match ip address prefix-list'>  word
match-ip-address-prefix-len = <'match ip address prefix-len'> number
(* match-ipv6-address-access-list = ipv6address *)
(* match-ipv6-address-prefix-list = word *)
match-ip-next-hop = <'match ip next-hop'> ipv4address
match-as-path = <'match as-path'> word
match-metric = <'match metric'> number
match-tag = (<'match tag'>  number)
match-local-preference = <'match local-preference'> number
match-community = <'match community'> word
(* match-peer-ipv4 = <'match peer'>  ipv4address
   match-peer-ipv6 = <'match peer'>  ipv6address *)
match-peer-interface-name = <'match peer'> word
match-source-protocol = <'match source-protocol'> word
match-source-instance = <'match source-instance'> number


(* route map set list *)
<route-map-set-list> = set-list1 | set-list2
<set-list1> = set-tag | set-ip-next-hop | set-ip-next-hop-peer-address | set-ip-next-hop-unchanged
<set-list2> = set-local-preference | set-weight | set-metric | set-as-path-prepend | set-community

set-tag = <'set tag'> word
set-ip-next-hop = <'set ip next-hop'> ipv4address
set-ip-next-hop-peer-address = <'set ip next-hop'> ipv4address
set-ip-next-hop-unchanged = <'set ip next-hop unchanged'>
(*
set ipv6 next-hop peer-address
set ipv6 next-hop prefer-global
set ipv6 next-hop global IPV6_ADDRESS
*)

(* route map set terminators *)
set-local-preference = <'set local-preference'> number
set-weight = <'set weight'> number
set-metric = <'set metric'> number
(* ? *)
set-as-path-prepend = <'set as-path prepend'> number
<set-community> = <'set community'> route-map-set-community ?[set-community-option]
route-map-set-community = community-type
set-community-option = additive
additive = <'additive'>
(* set ipv6 next-hop local IPV6_ADDRESS *)

community-perm = permit | deny
community-list-name = word
community = community-type

(* Primitives *)
<community-type> = number #":" number
<permit> = 'permit'
<deny> = 'deny'
<ipv4address> = address
<address> = #"\d+\.\d+\.\d+\.\d+"
<cidr> = (address '/' number)
<prefix> = cidr
<number> = #"[0-9]+"
<token> = word | number
<word> = #'[a-zA-Z0-9()\\.,-^?_|]+'
<number> = #'[0-9]+'

~~~


## Clojure SPEC Validator

#### (TODO Refine specs around commands)
```
; Specs
(s/def ::valid-lower-asn  (s/int-in 1 64495))
(s/def ::valid-higher-asn (s/int-in 64512 65535))
(s/def ::valid-asn (s/or :public  ::valid-lower-asn
                         :private ::valid-higher-asn))
(def ip-regex #"^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$")
(s/def ::ip-address-type  (s/spec (s/and string? #(re-matches ip-regex %))
                              :gen #(sg/string-generator ip-regex)))                                
(s/def ::asn ::valid-asn)
(s/def ::router-id ::ip-address-type)
(s/def ::otherkeys #{"multiple-instance" "synchronization" "no synchronization" "auto-summary" "no auto-summary"})   
(s/def ::best-path #{"confed" "multipath-relax" "compare-routerid"})
(s/def ::confederation-identifier (s/int-in 1 4294967295))
(s/def ::bgp-med-types #{"always-compare-med" "deterministic-med"})
(s/def ::bgp-bestpath-types #{"compare-routerid" "as-path confed"})
(s/def ::confederation-peers (s/coll-of ::valid-asn :kind set?))



(s/def ::bgp-confederation (s/keys :req-un [::confederation-identifier ::confederation-peers]))
(s/def ::bgp-bestpath (s/coll-of ::bgp-bestpath-types))
(s/def ::bgp-med (s/coll-of ::bgp-med-types))
(s/def ::bgp-global (s/keys :req-un [::asn ::otherkeys ::router-id]))


(s/def ::bgprouter (s/keys :req-un [::bgp-global
                                       ::bgp-med
                                       ::bgp-bestpath
                                       ::bgp-confederation]))

(s/def :unq/bgprouter  (s/keys :req-un [::bgprouter]))  
```

## Sample Generator based on spec (Obviously needs work)

~~~
{:device
 {:hostname "J",
  :interface-list
  [{:interface-name "eth0", :ip-cidr "10.0.18.1/24"}
   {:interface-name "eth1", :ip-cidr "10.0.19.1/24"}
   {:interface-name "eth2", :ip-cidr "10.0.15.2/24"}
   {:interface-name "eth3", :ip-cidr "10.0.13.2/24"}
   {:interface-name "eth4", :ip-cidr "10.0.11.2/24"}
   {:interface-name "eth5", :ip-cidr "10.0.9.2/24"}],
  :router
  {:bgp-global
   {:asn 65525,
    :otherkeys "no synchronization",
    :router-id "192.0.0.1"},
   :bgp-med ["always-compare-med" "deterministic-med"],
   :bgp-bestpath ["compare-routerid" "as-path confed"],
   :bgp-confederation
   {:confederation-identifier 100,
    :confederation-peers #{65529 65528 65527 65530}},
   :neighbor-list
   [{:neighbor {:neighbor-address "10.0.18.2", :remote-as 200}}
    {:neighbor {:neighbor-address "10.0.19.2", :remote-as 300}}
    {:neighbor {:neighbor-address "10.0.15.1", :remote-as 65527}}
    {:neighbor
     {:neighbor-address "10.0.15.1",
      :neighbor-options "next-hop-self"}}
    {:neighbor
     {:neighbor-address "10.0.15.1", :send-community-options "both"}}
    {:neighbor
     {:neighbor-address "10.0.15.1", :advertisement-interval "5"}}
    {:neighbor {:neighbor-address "10.0.13.1", :remote-as 65528}}
    {:neighbor
     {:neighbor-address "10.0.13.1",
      :neighbor-options "next-hop-self"}}
    {:neighbor
     {:neighbor-address "10.0.13.1", :send-community-options "both"}}
    {:neighbor
     {:neighbor-address "10.0.13.1", :advertisement-interval "5"}}
    {:neighbor {:neighbor-address "10.0.11.1", :remote-as 65529}}
    {:neighbor
     {:neighbor-address "10.0.11.1",
      :neighbor-options "next-hop-self"}}
    {:neighbor
     {:neighbor-address "10.0.11.1", :send-community-options "both"}}
    {:neighbor
     {:neighbor-address "10.0.11.1", :advertisement-interval "5"}}
    {:neighbor {:neighbor-address "10.0.9.1", :remote-as 65530}}
    {:neighbor
     {:neighbor-address "10.0.9.1", :neighbor-options "next-hop-self"}}
    {:neighbor
     {:neighbor-address "10.0.9.1", :send-community-options "both"}}
    {:neighbor
     {:neighbor-address "10.0.9.1", :advertisement-interval "5"}}
    {:neighbor
     {:neighbor-address "10.0.18.2",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.18.2",
      :neighbor-route-map
      {:route-map "rm-export-2", :direction "out"}}}
    {:neighbor
     {:neighbor-address "10.0.19.2",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.19.2",
      :neighbor-route-map
      {:route-map "rm-export-2", :direction "out"}}}
    {:neighbor
     {:neighbor-address "10.0.15.1",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.15.1",
      :neighbor-route-map
      {:route-map "rm-export-1", :direction "out"}}}
    {:neighbor
     {:neighbor-address "10.0.13.1",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.13.1",
      :neighbor-route-map
      {:route-map "rm-export-1", :direction "out"}}}
    {:neighbor
     {:neighbor-address "10.0.11.1",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.11.1",
      :neighbor-route-map
      {:route-map "rm-export-1", :direction "out"}}}
    {:neighbor
     {:neighbor-address "10.0.9.1",
      :neighbor-route-map {:route-map "rm-in", :direction "in"}}}
    {:neighbor
     {:neighbor-address "10.0.9.1",
      :neighbor-route-map
      {:route-map "rm-export-1", :direction "out"}}}]},
  :ip-prefix-list
  [{:prefix-list-name "pl-1",
    :prefix-perm "permit",
    :ip-prefix "1.0.0.0/24"}
   {:prefix-list-name "pl-2",
    :prefix-perm "deny",
    :ip-prefix "1.0.0.0/24"}
   {:prefix-list-name "pl-3",
    :prefix-perm "permit",
    :ip-prefix "1.0.1.0/24"}
   {:prefix-list-name "pl-4",
    :prefix-perm "deny",
    :ip-prefix "1.0.1.0/24"}
   {:prefix-list-name "pl-5",
    :prefix-perm "permit",
    :ip-prefix "2.0.0.0/24"}
   {:prefix-list-name "pl-6",
    :prefix-perm "deny",
    :ip-prefix "2.0.0.0/24"}
   {:prefix-list-name "pl-7",
    :prefix-perm "permit",
    :ip-prefix "2.0.1.0/24"}
   {:prefix-list-name "pl-8",
    :prefix-perm "deny",
    :ip-prefix "2.0.1.0/24"}
   {:prefix-list-name "pl-9",
    :prefix-perm "permit",
    :ip-prefix "0.0.0.0/0",
    :le "32"}
   {:prefix-list-name "pl-10",
    :prefix-perm "deny",
    :ip-prefix "0.0.0.0/0",
    :le "32"}],
  :ip-community-list
  [{:ip-community-list-standard
    {:community-list-name "cl-1",
     :community-perm "permit",
     :community "200:1"}}
   {:ip-community-list-standard
    {:community-list-name "cl-2",
     :community-perm "permit",
     :community "200:2"}}
   {:ip-community-list-standard
    {:community-list-name "cl-3",
     :community-perm "permit",
     :community "100:1"}}
   {:ip-community-list-standard
    {:community-list-name "cl-4",
     :community-perm "permit",
     :community "100:2"}}
   {:ip-community-list-standard
    {:community-list-name "cl-5",
     :community-perm "permit",
     :community "100:3"}}
   {:ip-community-list-standard
    {:community-list-name "cl-6",
     :community-perm "permit",
     :community "100:4"}}
   {:ip-community-list-standard
    {:community-list-name "LOCAL",
     :community-perm "permit",
     :community "100:4"}}],
  :ip-as-path-list
  [{:access-list-record
    {:path "path-1",
     :as-path-perm "permit",
     :as-path-regex "^\\(?(65527|65528|65529|65530)_"}}
   {:access-list-record
    {:path "path-3",
     :as-path-perm "permit",
     :as-path-regex "^\\(?300_"}}
   {:access-list-record
    {:path "path-4",
     :as-path-perm "permit",
     :as-path-regex "^\\(?200_"}}],
  :route-map-list
  [{:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "10",
     :match-ip-address-prefix-list "pl-1",
     :match-as-path "path-1",
     :route-map-set-community "100:1",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "20",
     :match-ip-address-prefix-list "pl-2"}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "30",
     :match-ip-address-prefix-list "pl-3",
     :match-as-path "path-1",
     :route-map-set-community "100:1",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "40",
     :match-ip-address-prefix-list "pl-4"}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "50",
     :match-ip-address-prefix-list "pl-5",
     :match-as-path "path-1",
     :route-map-set-community "100:2",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "60",
     :match-ip-address-prefix-list "pl-6"}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "70",
     :match-ip-address-prefix-list "pl-7",
     :match-as-path "path-1",
     :route-map-set-community "100:2",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "80",
     :match-ip-address-prefix-list "pl-8"}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "90",
     :match-ip-address-prefix-list "pl-9",
     :match-as-path "path-3",
     :route-map-set-community "100:3",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "100",
     :match-community "cl-1",
     :match-ip-address-prefix-list "pl-9",
     :match-as-path "path-1",
     :route-map-set-community "100:1",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "110",
     :match-ip-address-prefix-list "pl-9",
     :match-as-path "path-4",
     :set-local-preference "99",
     :route-map-set-community "100:4",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "120",
     :match-community "cl-2",
     :match-ip-address-prefix-list "pl-9",
     :match-as-path "path-1",
     :set-local-preference "99",
     :route-map-set-community "100:1",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-in",
     :route-map-perm "permit",
     :order "130",
     :match-ip-address-prefix-list "pl-10"}}
   {:route-map-record
    {:route-map-name "rm-export-1",
     :route-map-perm "permit",
     :order "10",
     :match-community "cl-3"}}
   {:route-map-record
    {:route-map-name "rm-export-1",
     :route-map-perm "permit",
     :order "20",
     :match-community "cl-4"}}
   {:route-map-record
    {:route-map-name "rm-export-1",
     :route-map-perm "permit",
     :order "30",
     :match-community "cl-5",
     :route-map-set-community "200:1",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-export-1",
     :route-map-perm "permit",
     :order "40",
     :match-community "cl-6",
     :route-map-set-community "200:2",
     :set-community-option [:additive]}}
   {:route-map-record
    {:route-map-name "rm-export-2",
     :route-map-perm "permit",
     :order "10",
     :match-community "cl-4"}}]}}
~~~
