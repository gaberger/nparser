(ns ncp-parser.core
 (:require [instaparse.core :as insta :refer [defparser]]
           [clojure.pprint :as pprint :refer [pprint]]))

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
  "BGP = ROUTERBGP
   <ROUTERBGP> = <'router bgp'> asn otherkeys routerid bgp
   asn = number
   otherkeys = 'no synchronization'
   bgp =  (<'bgp'> bgpkeys)*
   routerid = router-id 
   <bgpkeys> = <'router-id'> router-id | always-compare-med | deterministic-med | best-path | best-path-as-path | confederation-identifier | confederation-peers 
   <router-id> = (<'bgp router-id'> address)
   always-compare-med = 'always-compare-med'
   deterministic-med = 'deterministic-med' 
   best-path = <'bestpath'> token
   best-path-as-path = <'bestpath as-path'> token 
   confederation-identifier = <'confederation identifier'> number 
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
 :output-format :hiccup  
 :string-ci true
 :auto-whitespace :standard)

(defn bgp-transform [input]
  (insta/transform 
    {
        :BGP        (fn b [& arg] 
                     (let [general-opts (take 3 arg)
                           bgp-args (nth arg 3)
                           opts   (assoc {} :BGP (reduce conj {}  (into [] general-opts)))]
                      (conj opts bgp-args))) 
        :bgp        (fn c [& arg]
                     (assoc {} :bgp (reduce conj {} (into [] arg))))
        :confederation-peers (fn fn-confederation-peers 
                              [& arg]
                              (conj [] :confederation-peers (into [] (conj arg))))}
   input))


(defn -main []
  (pprint (bgp-transform (frr-bgp test1))))