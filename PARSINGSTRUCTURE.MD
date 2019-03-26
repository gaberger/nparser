### Parsing a configuration.


Network configurations follow a loose set of rules which are difficult to parse without leveraging a context free grammar such as BNF.

## Modeling a configuration.
## Entity Attribute Value

A network configuration is complicated because it follows an informal structure for defining the configuration properties. Some elements naturally fall into a key/value such as ```hostname router-1```, while others are more complicated such as router configuration stanzas

```
router bgp 65525
no synchronization
bgp router-id 192.0.0.1
bgp always-compare-med
bgp deterministic-med
bgp bestpath compare-routerid
bgp bestpath as-path confed
bgp confederation identifier 100
bgp confederation peers 65527 65528 65529 65530
```

There is an evident hierarchy with keys in first position such as:

```
hostname
interface
router
ip
route-map
```


Entities

First Order

```
hostname  - <hostname>
interface - <interface-name>
router - -
ip
route-map
```

Second order (<- parent)

```
ip <- interface
prefix-list
```


Third order
```
prefix-list
```

Fourth order
```
<name> <permission> <prefix> <- prefix-list

```
