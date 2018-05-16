(ns ncp-parser.spec
    (:require [clojure.spec.alpha :as s]
              ; [clojure.spec.gen.alpha :as gen]))  
              [miner.strgen :as sg]
              [clojure.test.check.generators :as gen])      
        

(def model
  [:bgprouter
    {:bgp-global
      {:asn 65525,
      :otherkeys "no synchronization",
      :router-id "192.0.0.1"}}
    {:bgp ["always-compare-med" "deterministic-med"]}
    {:bgp-bestpath ["compare-routerid" "as-path confed"]}
    {:bgp-confederation
      {:confederation-identifier "100",
      :confederation-peers #{65529 65528 65527 65530}}}])


              
; (s/def bgp-spec
;     (spec/keys :req [::x ::y (or ::secret (and ::user ::pwd))] :opt [::z]))

(s/def ::exit (s/and integer? #(>= % 0) #(< % 256)))

(s/def ::id uuid?)
(s/def ::state #{:available :suspended :decommissioned})
(s/def ::cpu int?)
(s/def ::ram int?)
(s/def ::disks int?)
(s/def ::valid-lower-asn  (s/int-in 1 64495))
(s/def ::valid-higher-asn (s/int-in 64512 65535))
(s/def ::valid-asn (s/or :public  ::valid-lower-asn
                         :private ::valid-higher-asn))
       
; (def s (s/cat :BGP (s/keys :req-un [::BGP])
;               :odds (s/+ ::odd?)
;               :m (s/keys :req-un [::a ::b ::c])
;               :oes (s/* (s/cat :o ::odd? :e ::even?))
;               :ex (s/alt :odd ::odd? :even ::even?)))


(def ip-regex #"^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$")
(s/def ::ip-address-type  (s/spec (s/and string? #(re-matches ip-regex %))
                              :gen #(sg/string-generator ip-regex)))                                
(s/def ::asn ::valid-asn)
(s/def ::routerid ::ip-address-type)
(s/def ::otherkeys string?)   
(s/def ::always-compare-med string?) 
(s/def ::deterministic-med string?)
(s/def ::best-path #{"confed" "multipath-relax" "compare-routerid"})
(s/def ::best-path-as-path string?)
(s/def ::confederation-identifier string?)
(s/def ::confederation-peers (s/coll-of ::valid-asn :kind set?))

(s/def :unq/bgp 
  (s/keys :req-un [::always-compare-med ::deterministic-med ::best-path ::best-path-as-path ::confederation-identifier ::confederation-peers]))     
  ; (s/keys :req-un [::always-compare-med ::deterministic-med ::best-path]))   

(s/def :unq/router
  (s/keys :req-un [::asn ::routerid ::otherkeys :unq/bgp]))     

(defn gen-config
  "Mock function to generate config"
  []
  (gen/sample (s/gen :unq/router)))

(print (s/conform :unq/router model))

; (s/def ::port (s/conformer x-integer?))
; (s/def ::nested-data (s/keys :req-un [::port]))
; (s/def ::config (s/keys :req [::nested-data]))


; (s/conform ::config {::nested-data {:port "12345"}})


