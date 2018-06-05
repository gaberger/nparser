(ns ncp-parser.frr.generator
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.zipper :as SZ]
            [clojure.zip :as zip]
            [clojure.walk :refer [postwalk prewalk]]
            [clojure.string :as str]
            [spyscope.core :refer :all]))

(defonce container (atom []))

(def key-regex #"^(\<\w+\>|\+[a-zA-Z-_]+)$")
(def options-regex #"^\+")

(defn is-skip-tag? [tag]
   (some? (re-matches key-regex (name tag))))

(defn is-transpose-tag? [tag]
   (some? (re-find #"_" (name tag))))

(defn is-options-tag? [tag]
  (some? (re-find options-regex (name tag))))

(defn is-regular-tag? [tag]
  (and  (not (is-skip-tag? tag))
        (not (is-transpose-tag? tag))
        (not (is-options-tag? tag))))

(defn transpose-tag [tag]
    (str (str/join \space (str/split (name tag) #"_")) \space))

(defn skip-tag [tag]
  (when-not (is-skip-tag? tag)
            (str (name tag) \space)))

(defn regular-tag [tag]
    (let [tag-string (str (name tag) \space)]
      (swap! container conj tag-string)))

; (defn keyword-handler [tag]
;   (cond
;    (is-transpose-tag? tag)  (transpose-tag tag)
;    (is-regular-tag? tag)    (regular-tag  tag)))

(defn keyword-handler [tag]
   (if-let [tag (some->>  tag skip-tag transpose-tag)]
      (do
        (println "adding keyword -->> " (str (name  tag)))
        (swap! container conj (str (name  tag))))))

(defn vector-handler [node]
  (condp #(= (first %2) %1)  node
    :ip_address   (println "found ip_address tag --> " (rest node))
    node))

(defn set-handler [node]
   (println "adding set -->> " node)
   (swap! container conj (str (str/join \space node) \newline)))

(defn string-handler [node]
  (println "adding string -->> " node)
  (swap! container conj (str node \newline)))

(defn number-handler [node]
  (println "adding number -->> " node)
  (swap! container conj (str node \newline)))

(defn options-handler [node]
  (if (and (not (map? (first node)))
           (= (count node) 2))
      (let [[option value] node]
        (if (is-options-tag? option)
          (let [fmt-option (subs (transpose-tag option) 1)]
               (if
                 (boolean value)
                 (do
                   (println "adding option -->> "  fmt-option)
                   (swap! container conj (str fmt-option \newline)))
                 (swap! container conj (str "no " fmt-option \newline)))
            ; (println "found options tag" (first node)  " --> " (rest node))
             node))
        node)))

; (defn check-vectors [node]
;   (condp #(= (first %2) %1) node
;     :ip_address (println "found ip_address in vector")
;     :hostname (println "found hostname in vector")
;     boolean? (println "found boolean in vector")
;     node))

(defn gen-config [node]
  (cond
    (string? node)  (string-handler node)
    (set? #spy/p node) (set-handler node)
    (number?  node)  (number-handler node)
    (keyword? node) (keyword-handler node)
    (vector? node) (options-handler node))
  node)

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
    :router_bgp {:<asn>           65525,
                 :+synchronization false
                 :<bgplist>
                    [{:bgp  {:router-id       "192.0.0.1",}}
                     {:bgp  {:+always-compare-med true}}
                     {:bgp  {:+deterministic-med  true}}
                     {:bgp  {:bestpath {:+compare-routerid true}}}
                     {:bgp  {:bestpath {:+as-path_confed true}}}
                     {:bgp  {:confederation  {:identifier 100}}}
                     {:bgp  {:confederation  {:peers #{65529 65528 65527 65530},}}}]
                   :<neighbors>
                                   [{:neighbor {:10.0.18.2 {:remote-as 200}}}
                                    {:neighbor {:10.0.19.2 {:remote-as 300}}}
                                    {:neighbor {:10.0.15.1 {:remote-as              65527
                                                            :next-hop-self          true
                                                            :send-community "both"
                                                            :advertisement-interval "5"}}}
                                    {:neighbor {:10.0.13.1              {:remote-as 65528}
                                                :+next-hop-self          true
                                                :send-community "both"
                                                :advertisement-interval "5"}}
                                    {:neighbor {:10.0.18.2 {:<route-maps>
                                                            [{:route-map {:<policy> "rm-in", :<direction> "in"}}
                                                             {:route-map {:<policy> "rm-export-2", :<direction> "out"}}]}}}]}}})
(defn output_config []
  (postwalk gen-config model)
  (print (str/join @container)))
