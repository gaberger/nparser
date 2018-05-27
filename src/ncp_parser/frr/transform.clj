(ns ncp-parser.frr.transform
 (:require [instaparse.core :as insta :refer [transform]]))


(defn frr-transform [input]
  (transform
    {
        :asn                      (fn asn [arg]
                                       (assoc {} :asn (clojure.edn/read-string arg)))
        :remote-as                (fn remote-as [arg]
                                      (assoc {} :remote-as (clojure.edn/read-string arg)))
        :ip-cidr                   (comp #(conj [] :ip-cidr %) str)
        :bgp-med                   (fn bgp-med [& arg]
                                     (assoc {} :bgp-med (into [] arg)))
        :bgp-bestpath              (fn bgp-bestpath [& arg]
                                       (assoc {} :bgp-bestpath (into [] arg)))
        :interface                 (fn interface [& arg]
                                     (reduce conj {} arg))
        :interface-list            (fn interface-list [& arg]
                                       (assoc {} :interface-list (into [] arg)))
        :ip-prefix-list            (fn ip-prefix-list [& arg]
                                       (assoc {} :ip-prefix-list (into [] arg)))
        :ip-prefix                 (comp #(conj [] :ip-prefix %) str)
        :ip-prefix-record          (fn ip-prefix-record [& arg]
                                     (reduce conj {} arg))
        :community                 (comp #(conj [] :community %) str)
        :route-map-set-community       (comp #(conj [] :route-map-set-community %) str)

        :ip-community-list-standard  (fn icls [& arg]
                                          (assoc {} :ip-community-list-standard (reduce conj {} arg)))
        :ip-community-list-extended  (fn icle [& arg]
                                          (assoc {} :ip-community-list-extended (reduce conj {} arg)))
        :ip-community-list           (fn ip-community-list [& arg]
                                       (assoc {} :ip-community-list (into [] arg)))
        :ip-as-path-list            (fn ip-as-path-list [& arg]
                                      (assoc {} :ip-as-path-list (into [] arg)))
        :route-map-list             (fn route-map-list [& arg]
                                      (assoc {} :route-map-list (into [] arg)))
        :neighbor-list            (fn neighbor-list [& arg]
                                    (assoc {} :neighbor-list (into [] arg)))
        :hostname                 (fn hostname [& arg]
                                        (assoc {} :hostname (first arg)))
        :router                     (fn router [& arg]
                                        (assoc {} :router (into {} arg)))
        :access-list-record        (fn access-list-record  [& arg]
                                     (assoc {} :access-list-record (reduce conj {} arg)))
        :bgp-global                (fn bgp-global  [& arg]
                                     (assoc {} :bgp-global (reduce conj {} arg)))
        :neighbor-route-map        (fn neighbor-route-map [& arg]
                                    (conj [:neighbor-route-map] (reduce conj {} arg)))
        :neighbor                  (fn neighbor [& arg]
                                       (assoc {} :neighbor (reduce conj {} arg)))
        :route-map-record          (fn route-map-record [& arg]
                                       (assoc {} :route-map-record (reduce conj {} arg)))
        :bgp-confederation          (fn c [& arg]
                                      (assoc {} :bgp-confederation (reduce conj {} arg)))
        :confederation-peers        (fn cp [& arg]
                                      (conj [] :confederation-peers (into #{} (map #(clojure.edn/read-string %) arg))))
        :confederation-identifier   (fn ci [arg]
                                       {:confederation-identifier  (clojure.edn/read-string arg)})
        :device                     (fn device [& arg]
                                       (assoc {} :device (into {} arg)))}
   input))