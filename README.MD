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

```
{:bgprouter
  {:bgp-global
   {:asn 1,
    :otherkeys "no synchronization",
    :router-id "0.46.253.166"},
   :bgp-med
   ["deterministic-med"
    "deterministic-med"
    "always-compare-med"
    "deterministic-med"
    "deterministic-med"
    "deterministic-med"
    "always-compare-med"
    "always-compare-med"
    "always-compare-med"
    "always-compare-med"
    "always-compare-med"
    "deterministic-med"
    "always-compare-med"],
   :bgp-bestpath
   ["compare-routerid"
    "compare-routerid"
    "compare-routerid"
    "as-path confed"
    "as-path confed"
    "as-path confed"
    "as-path confed"
    "as-path confed"
    "compare-routerid"
    "as-path confed"
    "compare-routerid"
    "compare-routerid"],
   :bgp-confederation
   {:confederation-identifier 233, :confederation-peers #{60 64513}}}})

```
