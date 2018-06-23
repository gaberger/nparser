(ns nparser.frr.transforms.v1.core
  (:require [clojure.string :as str]
            [instaparse.core :as insta :refer [transform]]))

(defn av
  [args]
  (let [[a v] args]
    (assoc {} (keyword a) v)))

(defn eavl
  "Take keyword as entity and collection as args and return args as vector to key"
  [e args]
  (assoc {} e (into [] args)))

(defn eav
  "Take keyword as entity and collection as args and return args as is"
  [e args]
  (assoc {} e (first args)))

(defn chtobool [k args]
  (let [attr (str/replace (first args) #" " "_")
        kton (name k)
        ntok (keyword (str "+" kton))]
    (cond
      (= attr "no") (assoc {} ntok false)
      (= attr kton) (assoc {} ntok true)
      :default args)))



(defn transformer [input]
  (transform
    {:asn                    (fn asn [arg]
                               (assoc {} :<asn> (clojure.edn/read-string arg)))
     :ip_address             (comp #(conj [] :ip_address %) str)
     :name                   (comp #(conj [] :<name> %) str)
     :interface              (fn interfaces [& arg]
                               (assoc {} :interface
                                         (reduce conj {} arg)))
     :bgplist                (fn bgplist [& arg] (assoc {} :<bgplist> (into [] arg)))
     :bestpath               (fn bestpath [& arg]
                               (assoc {} :bestpath
                                         (reduce conj {} arg)))
     :synchronization        (fn sync [& args] (chtobool :synchronization args))
     :always-compare-med     (fn sync [& args] (chtobool :always-compare-med args))
     :deterministic-med      (fn sync [& args] (chtobool :deterministic-med args))
     :compare-routerid       (fn sync [& args] (chtobool :compare-routerid args))
     :as-path_confed         (fn sync [& args] (chtobool :as-path_confed args))
     :map                    (fn m [& args] (av args))
     :route-map              #(assoc {} :route-map %)
     :remote-as              #(assoc {} :remote-as (clojure.edn/read-string %))
     :send-community         #(assoc {} :send-community %)
     :advertisement-interval #(assoc {} :advertisement-interval %)
     :next-hop-self          #(assoc {} :+next-hop-self true)
     :naddr                  (fn m [& args] (av args))
     :hostname               (fn m [& args] (eav :hostname args))
     :router_bgp             (fn m [& args] (eavl :router_bgp args))
     :neighbor               (fn m [& args] (eav :neighbor args))
     :neighbors              (fn m [& args] (eavl :<neighbors> args))
     :interfaces             (fn m [& args] (eavl :<interfaces> args))
     :device                 (fn m [& args] (eavl :<device> args))
     :bgp                    (fn bgp [& arg]
                               (assoc {} :bgp
                                         (reduce conj {} arg)))
     :peers                  (fn peers [& arg]
                               (assoc {} :peers (conj (into (sorted-set) arg))))
     :identifier             (fn asn [arg]
                               (assoc {} :identifier (clojure.edn/read-string arg)))}
    input))