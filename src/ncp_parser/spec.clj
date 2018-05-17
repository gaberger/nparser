(ns ncp-parser.spec
    (:require [clojure.spec.alpha :as s]
              [clojure.spec.gen.alpha :as sgen]
              [miner.strgen :as sg]
              [expound.alpha :as expound]
              [clojure.pprint :as pprint :refer [pprint]]))



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
   

(s/def ::bgprouter (s/keys :req-un [   ::bgp-global
                                       ::bgp-med
                                       ::bgp-bestpath
                                       ::bgp-confederation]))

(s/def :unq/bgprouter  (s/keys :req-un [::bgprouter]))  
                                        

(def model
 {:bgprouter
  {:bgp-global
    {:asn 65525
     :otherkeys "no synchronization"
     :router-id "192.0.0.1"}
   :bgp-med ["always-compare-med" "deterministic-med"],
   :bgp-bestpath ["compare-routerid" "as-path confed"]
   :bgp-confederation
      {:confederation-identifier 100
       :confederation-peers #{65529 65528 65527 65530}}}})

(defn validate
  []
  (s/conform :unq/bgprouter model))

(defn gen-config
  "Mock function to generate config"
  []
  (pprint (sgen/sample (s/gen :unq/bgprouter))))

