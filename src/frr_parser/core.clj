(ns frr-parser.core
 (:require [instaparse.core :as insta :refer [defparser]]))

(def test1
 "router bgp 65525
    no synchronization
    bgp router-id 192.0.0.1
    bgp always-compare-med
    bgp deterministic-med
    bgp bestpath compare-routerid
    bgp bestpath as-path confed
    bgp confederation identifier 100
    bgp confederation peers 65527 65528 65529 65530")


; (comment
;   router bgp 65525
;     no synchronization
;     bgp router-id 192.0.0.1
;     bgp always-compare-med
;     bgp deterministic-med
;     bgp bestpath compare-routerid
;     bgp bestpath as-path confed
;     bgp confederation identifier 100
;     bgp confederation peers 65527 65528 65529 65530
;     neighbor 10.0.18.2 remote-as 200
;     neighbor 10.0.19.2 remote-as 300
;     neighbor 10.0.15.1 remote-as 65527
;     neighbor 10.0.15.1 next-hop-self
;     neighbor 10.0.15.1 send-community both
;     neighbor 10.0.15.1 advertisement-interval 5
;     neighbor 10.0.13.1 remote-as 65528
;     neighbor 10.0.13.1 next-hop-self
;     neighbor 10.0.13.1 send-community both
;     neighbor 10.0.13.1 advertisement-interval 5
;     neighbor 10.0.11.1 remote-as 65529
;     neighbor 10.0.11.1 next-hop-self
;     neighbor 10.0.11.1 send-community both
;     neighbor 10.0.11.1 advertisement-interval 5
;     neighbor 10.0.9.1 remote-as 65530
;     neighbor 10.0.9.1 next-hop-self
;     neighbor 10.0.9.1 send-community both
;     neighbor 10.0.9.1 advertisement-interval 5
;     neighbor 10.0.18.2 route-map rm-in in
;     neighbor 10.0.18.2 route-map rm-export-2 out
;     neighbor 10.0.19.2 route-map rm-in in
;     neighbor 10.0.19.2 route-map rm-export-2 out
;     neighbor 10.0.15.1 route-map rm-in in
;     neighbor 10.0.15.1 route-map rm-export-1 out
;     neighbor 10.0.13.1 route-map rm-in in
;     neighbor 10.0.13.1 route-map rm-export-1 out
;     neighbor 10.0.11.1 route-map rm-in in
;     neighbor 10.0.11.1 route-map rm-export-1 out
;     neighbor 10.0.9.1 route-map rm-in in
;     neighbor 10.0.9.1 route-map rm-export-1 out)


(defparser frr-bgp
  "ROUTERBGP = BGP
   <BGP> = 'router bgp' <number> otherkeys bgp
   otherkeys = 'no synchronization'
   bgp =  (<'bgp'> bgpkeys)*
   <bgpkeys> = <'router-id'> router-id | 'always-compare-med' | 'deterministic-med' | 'bestpath' token | 'bestpath as-path' token | 'confederation identifier' number | confederation-peers 
   router-id = address
   confederation-peers = <'confederation peers'> number+
   <sentence> = token (<whitespace> token)*
   whitespace = #'\\s+'
   symbol = #'[.]'
   <numchar> = number word | word number
   <token> = word | numchar
   <word> = #'[a-zA-Z()-\\.,]+'
   <address> = #'\\d+\\.\\d+\\.\\d+\\.\\d+'
   <newline> = #'\\n' | #'\\r\\n'
   <number> = #'[0-9]+'"
 :output-format :enlive  
 :string-ci true
 :auto-whitespace :standard)


  
