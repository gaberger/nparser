(ns nparser.frr.spec
    (:require [clojure.spec.alpha :as s]
              [clojure.spec.gen.alpha :as sgen]
              [clojure.java.io :as io]
              [miner.strgen :as sg]))

; Specs
(s/def ::valid-lower-asn  (s/int-in 1 64495))
(s/def ::valid-higher-asn (s/int-in 64512 65535))
(s/def ::valid-asn (s/or :public  ::valid-lower-asn
                         :private ::valid-higher-asn))
(def ip-regex #"^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])$")
(def ip-prefix #"^(([01]?\d\d?|2[0-4]\d|25[0-5])\.){3}([01]?\d\d?|2[0-4]\d|25[0-5])\/([1-9]|[12][0-9]|3[012])$")
(def prefix-wildcard #"^0\.0\.0\.0\/0$")
(s/def ::ip-address-type  (s/spec (s/and string? #(re-matches ip-regex %))
                              :gen #(sg/string-generator ip-regex)))
(s/def ::ip-prefix-type  (s/spec (s/and string? #(re-matches ip-prefix %))
                              :gen #(sg/string-generator ip-prefix)))
(s/def ::ip-prefix-wild (s/spec (s/and string? #(re-matches prefix-wildcard %))
                              :gen #(sg/string-generator prefix-wildcard)))

(s/def ::asn ::valid-asn)
(s/def ::router-id ::ip-address-type)
(s/def ::otherkeys #{"multiple-instance" "synchronization" "no synchronization" "auto-summary" "no auto-summary"})
(s/def ::best-path #{"confed" "multipath-relax" "compare-routerid"})
(s/def ::confederation-identifier (s/int-in 1 4294967295))
(s/def ::bgp-med-types #{"always-compare-med" "deterministic-med"})
(s/def ::bgp-bestpath-types #{"compare-routerid" "as-path confed"})
(s/def ::direction #{"in" "out"})
(s/def ::confederation-peers (s/coll-of ::valid-asn :kind set?))
(s/def ::hostname string?)
(s/def ::interface-name string?)
(s/def ::ip-address ::ip-address-type)
(s/def ::ip-cidr (s/or :wild ::ip-prefix-wild
                       :standard ::ip-prefix-type))
(s/def ::route-map string?)
(s/def ::neighbor-route-map (s/keys :req-un [::route-map ::direction]))
; TODO Fix this
(s/def ::advertisement-interval string?)
(s/def ::neighbor (s/keys :req-un [::neighbor-address]
                          :opt-un [::remote-as ::neighbor-route-map ::advertisement-interval]))
(s/def ::neighbors (s/keys :req-un [::neighbor]))
(s/def ::neighbor-address ::ip-address-type)
(s/def ::remote-as ::asn)
(s/def ::neighbor-list (s/coll-of ::neighbors))
(s/def ::perms #{"permit" "deny"})
(s/def ::prefix-list-name string?)
(s/def ::prefix-perm ::perms)
(s/def ::ip-prefix  (s/or :wild ::ip-prefix-wild
                       :standard ::ip-prefix-type))

(s/def ::ip-prefixes (s/keys :req-un [::prefix-list-name ::prefix-perm ::ip-prefix]))
(s/def ::ip-prefix-list (s/coll-of ::ip-prefixes))


(s/def ::community-list-name string?)
(s/def ::community-perm ::perms)
(s/def ::community string?)
; TODO Fix
(s/def ::ip-community-list-standard (s/keys :req-un [::community-list-name ::community-perm ::community]))
(s/def ::ip-community-list-expanded (s/keys :req-un [::community-list-name ::community-perm ::community]))
(s/def ::ip-community-list-standard-key (s/keys :req-un [::ip-community-list-standard]))
(s/def ::ip-community-list-expanded-key (s/keys :req-un [::ip-community-list-expanded]))
(s/def ::ip-community-list-expanded (s/keys :req-un [::community-list-name ::community-perm ::community]))
(s/def ::ip-communities (s/keys :req-un [::ip-community-type]))
(s/def ::ip-community-list (s/or :standard (s/coll-of ::ip-community-list-standard-key)
                                 :expanded (s/coll-of ::ip-community-list-expanded-key)))

(s/def ::path string?)
(s/def ::as-path-perm ::perms)
; TODO Fix
(s/def ::as-path-regex string?)

(s/def ::access-list-record (s/keys :req-un [::path ::as-path-perm ::as-path-regex]))
(s/def ::access-list-records (s/keys :req-un [::access-list-record]))
(s/def ::ip-as-path-list (s/coll-of ::access-list-records))



(s/def ::route-map-name string?)
(s/def ::route-map-perm ::perms)
; TODO
(s/def ::order string?)
(s/def ::match-ip-address-prefix-list string?)
(s/def ::match-as-path string?)
(s/def ::route-map-set-community string?)
(s/def ::set-community-options #{:additive})
(s/def ::set-community-option (s/coll-of ::set-community-options))

; (s/def ::route-map-record (s/keys ::opt-un [::route-map-name ::route-map-perm ::order ::match-ip-address-prefix-list ::match-as-path
;                                             ::route-map-set-community ::set-community-option]))
;
;
;
(s/def ::route-map-record (s/keys :req-un [::route-map-name ::route-map-perm ::order]
                                  :opt-un [::match-ip-address-prefix-list ::match-as-path ::route-map-set-community ::set-community-option]))

(s/def ::route-map-records (s/keys :req-un [::route-map-record]))
(s/def ::route-map-list (s/coll-of ::route-map-records))

(s/def ::bgp-confederation (s/keys :req-un [::confederation-identifier ::confederation-peers]))
(s/def ::bgp-bestpath (s/coll-of ::bgp-bestpath-types))
(s/def ::bgp-med (s/coll-of ::bgp-med-types))
(s/def ::bgp-global (s/keys :req-un [::asn ::otherkeys ::router-id]))

(s/def ::router (s/keys :req-un [::bgp-global ::bgp-med ::bgp-bestpath ::bgp-confederation ::neighbor-list]))

(s/def ::interfaces (s/keys :req-un [::interface-name ::ip-cidr]))
(s/def ::interface-list (s/coll-of ::interfaces))
(s/def ::device (s/keys :req-un [::hostname ::interface-list ::router ::ip-prefix-list ::ip-community-list ::ip-as-path-list ::route-map-list]))

; main form
(s/def :unq/device (s/keys :req-un [::device]))

; (defn expound
;   []
;   (expound/expound :unq/device (read-string (slurp (io/resource "models/frr/model.edn")))))
; ;
; (defn conform
;   []
;   (s/conform :unq/device (read-string (slurp (io/resource "models/frr/model.edn")))))
;
; (defn valid
;   []
;   (s/valid? :unq/device (read-string (slurp (io/resource "models/frr/model.edn")))))
; ;
; (defn explain
;   []
;   (s/explain :unq/device (read-string (slurp (io/resource "models/frr/model.edn")))))
; ;
; ;
; (defn gen-config
;   "Mock function to generate config"
;   []
;   (pprint (sgen/sample (s/gen :unq/device))))
