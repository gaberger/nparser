## Network Configuration Parser 
This is an example of a Network Configuration Parser written in Clojure and using the Instaparse library

### Dependencies
Install Clojure and command line tools ( see [Getting Started](https://clojure.org/guides/getting_started) )

## Run program

```clj -m ncp-parser.core```


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

```
(defparser frr-bgp
  "bgprouter = bgp-global bgp-med bgp-bestpath bgp-confederation
  
   bgp-global =  (otherkeys | asn | router-id)*
   asn =  <'router bgp'> number
   otherkeys = 'no synchronization'
   
   bgp-med = (<'bgp'> bgp)*
           
   <bgp> = 'always-compare-med' | 'deterministic-med'
           
   always-compare-med = 'always-compare-med' 
   deterministic-med = 'deterministic-med'        
 
   router-id = (<'bgp router-id'> address)
           
   bgp-bestpath = &'bgp bestpath' (<'bgp bestpath'> best-path)*
   <best-path> = ('as-path confed' | 'as-path multipath-relax' | 'compare-routerid') 
           
   bgp-confederation = &'bgp confederation' (<'bgp confederation'> confederation)*
   <confederation> = (confederation-identifier | confederation-peers)
   confederation-identifier =  <'identifier'> number+       
   confederation-peers = <'peers'> number+

   <address> = #'\\d+\\.\\d+\\.\\d+\\.\\d+'
   <number> = #'[0-9]+'"
 :output-format :hiccup  
 :string-ci true
 :auto-whitespace :standard)

 ```



## Sample Output

```
#> clj -m ncp-parser.core
{:bgprouter
 {:bgp-global
  {:asn 65525,
   :otherkeys "no synchronization",
   :router-id "192.0.0.1"},
  :bgp-med ["always-compare-med" "deterministic-med"],
  :bgp-bestpath ["compare-routerid" "as-path confed"],
  :bgp-confederation
  {:confederation-identifier 100,
   :confederation-peers #{65529 65528 65527 65530}}}}
```


## Clojure SPEC Validator (TODO Refine specs around comands)
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