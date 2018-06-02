(ns ncp-parser.frr.generator
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.zipper :as SZ]
            [clojure.zip :as zip]
            [clojure.walk :refer [postwalk]]
            [clojure.string :as str]
            [spyscope.core :refer :all]))


(def container (atom []))
(def key-regex #"^\<\w+\>$")

(defn walk-eval [node]
  (if (map?  node)
      (condp = #spy/p node
        (select-keys node [:hostname]) (do (println "-->" node) node)
        (select-keys node [:<name>])  (do (println "-->" node) node)
        (select-keys node [:interface])  (do (println "-->" node) node)
        (select-keys node [:router])  (do (println "-->" node) node)
        node)
      (if (keyword? #spy/p node)
          (if (not (re-matches key-regex (name node)))
              (do (println "-->>" (name node) node))))))


(def model
  {:<device>
   {:hostname {:<name> "J"}
    :<interfaces>
              [{:interface {:<name> "eth0", :ip_address "10.0.18.1/24"}}
               {:interface {:<name> "eth1", :ip_address "10.0.19.1/24"}}
               {:interface {:<name> "eth2", :ip_address "10.0.15.2/24"}}
               {:interface {:<name> "eth3", :ip_address "10.0.13.2/24"}}
               {:interface {:<name> "eth4", :ip_address "10.0.11.2/24"}}
               {:interface {:<name> "eth5", :ip_address "10.0.9.2/24"}}]
    :router
              {:bgp
                                   {:<asn>           65525,
                                    :synchronization false
                                    :router-id       "192.0.0.1"},
               :always-compare-med true
               :deterministic-med  true
               :bestpath           {:compare-routerid true
                                    :as-path_confed   true}
               :confederation      {:identifier 100,
                                    :peers      #{65529 65528 65527 65530}},
               :<neighbors>
                                   [{:neighbor {:10.0.18.2 {:remote-as 200}}}
                                    {:neighbor {:10.0.19.2 {:remote-as 300}}}
                                    {:neighbor {:10.0.15.1 {:remote-as              65527
                                                            :next-hop-self          true
                                                            :send-community-options "both"
                                                            :advertisement-interval "5"}}}
                                    {:neighbor {:10.0.13.1              {:remote-as 65528}
                                                :next-hop-self          true
                                                :send-community-options "both"
                                                :advertisement-interval "5"}}
                                    {:neighbor {:10.0.18.2 {:route-map [{:<policy> "rm-in", :<direction> "in"}
                                                                        {:<policy> "rm-export-2", :<direction> "out"}]}}}]}}})
