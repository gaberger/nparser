##Transformation

Keys terminate in strings
Keys can be filtered out of output using "<>"
Keys can be concatenated using `_` for easier grouping
Quoted strings need to be escaped \"

### Keyword position

```
<token> = Filter out keyword
snake_case = Transformation into "snake case"

```



```
{:<device>
 {:hostname "foobar",
  :service "integrated-vtysh-config",
  :<interfaces>
   [{:interface
     {:<name> "GigEthernet0/0/0",
      :description "\"Faces Leaf switch 1\"",
      :ip_address "10.1.1.1/24"}}
    {:interface
     {:<name> "GigEthernet0/0/1",
      :description "\"Faces Spine switch 1\"",
      :ip_address "10.1.2.1/24"}}
    {:interface
     {:<name> "None0", :description "\"For blackholing traffic\""}}
    {:interface 
     {:<name> "loop0", :ip_address "10.10.1.1/32"}}],
  :router-id "10.10.1.1",
  :router_bgp
  [{:<asn> 65000}
   {:<neighbors>
    [{:neighbor {:LEAF "peer-group"}}
     {:neighbor {:RR "peer-group"}}
     {:neighbor {:TEST "peer-group"}}
     {:neighbor {:UNDEFINED "peer-group"}}
     {:neighbor {:10.1.1.2 {:remote-as 64001}}}
     {:neighbor {:10.1.1.2 {:peer-group "LEAF"}}}
     {:neighbor {:10.1.2.2 {:remote-as 73003}}}
     {:neighbor {:10.1.2.2 {:peer-group "UNDEFINED"}}}
     {:neighbor {:10.1.2.2 {:update-source "10.1.2.1"}}}]}
   {:<afiu>
    [{:address-family "ipv4 unicast"}
     {:<afneighbors>
      [{:neighbor {:LEAF "addpath-tx-all-paths"}}
       {:neighbor {:LEAF "soft-reconfiguration inbound"}}
       {:neighbor {:RR "soft-reconfiguration inbound"}}]}
     {:<exit-address-family> "exit-address-family"}
     {:address-family "ipv6 unicast"}
     {:<afneighbors>
      [{:neighbor {:LEAF "soft-reconfiguration inbound"}}]}
     {:<afneighbors>
      [{:neighbor {:TEST "soft-reconfiguration inbound"}}]}
     {:<exit-address-family> "exit-address-family"}]}],
  :vnc
  [{:<vncdefaults> "defaults"}
   {:response-lifetime 3600}
   {:<exitvnc> "exit-vnc"}],
  :line "vty"}}

```