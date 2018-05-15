## Network Configuration Parser 
This is an example of a Network Configuration Parser written in Clojure and using the Instaparse library

### Dependencies
Install Clojure and command line tools ( see [Getting Started](https://clojure.org/guides/getting_started) )

## Run program

clj -m frr-parser.core


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


## Sample Output


```
#> clj -m ncp-parser.core
{:BGP
 {:asn "65525",
  :otherkeys "no synchronization",
  :routerid "192.0.0.1"},
 :bgp
 {:always-compare-med "always-compare-med",
  :deterministic-med "deterministic-med",
  :best-path "compare-routerid",
  :best-path-as-path "confed",
  :confederation-identifier "100",
  :confederation-peers ["65527" "65528" "65529" "65530"]}}
```

