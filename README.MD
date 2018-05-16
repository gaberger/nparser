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
  "bgprouter = bgp-global bgp bgp-bestpath bgp-confederation
  
   bgp-global =  (otherkeys | asn | router-id)*
   asn =  <'router bgp'> number
   otherkeys = 'no synchronization'
   
   bgp = (<'bgp'> bgp-options)*
           
   <bgpkeys> = (router-id | bgp-options)
           
   <bgp-options> = 'always-compare-med' | 'deterministic-med'
           
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
[:bgprouter
 {:bgp-global
  {:asn 65525,
   :otherkeys "no synchronization",
   :router-id "192.0.0.1"}}
 {:bgp ["always-compare-med" "deterministic-med"]}
 {:bgp-bestpath ["compare-routerid" "as-path confed"]}
 {:bgp-confederation
  {:confederation-identifier "100",
   :confederation-peers #{65529 65528 65527 65530}}}]
```

