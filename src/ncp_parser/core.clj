(ns ncp-parser.core
 (:require [instaparse.core :as insta :refer [defparser]]
           [clojure.pprint :as pprint :refer [pprint]]
           [clojure.string :as str]))

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

(declare frr-bgp)

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

(defn parse-bp [arg]
  (assoc {} :bgprouter (reduce conj {} arg)))

(defn bgp-transform [input]
  (insta/transform 
    {
        :bgprouter                 (fn bgprouter [& arg]
                                     (assoc {} :bgprouter (reduce conj {} arg)))
        :asn                       (fn asn [arg] 
                                      (let [asnval (clojure.edn/read-string arg)]
                                       {:asn asnval}))
        :bgp-global                (fn c [& arg]
                                     (assoc {} :bgp-global (reduce conj {} arg)))
        :bgp-med                   (fn c [& arg]
                                     (assoc {} :bgp-med (into [] arg)))
        :bgp-bestpath               (fn c [& arg]
                                     (assoc {} :bgp-bestpath (into [] arg)))
        :bgp-confederation          (fn c [& arg]
                                      (assoc {} :bgp-confederation (reduce conj {} arg)))
        :confederation-peers        (fn cp [& arg]
                                      (conj [] :confederation-peers (into #{} (map #(clojure.edn/read-string %) arg))))
        :confederation-identifier   (fn ci [arg] 
                                       {:confederation-identifier  (clojure.edn/read-string arg)})}     
   input))

(defn -main []
  (pprint (bgp-transform (frr-bgp test1))))
