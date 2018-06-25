(ns nparser.frr.transforms.v3.core
  (:require [clojure.string :as str]
            [instaparse.core :as insta :refer [transform]]
            [spyscope.core :refer :all]))

(defn av
  [args]
  (let [[a v] args]
    (conj [] (keyword a) v)))

(defn eavf
  "Take keyword as entity and collection as args and return args as is"
  [e & args]
  (assoc {} e (into [] (first args))))

(defn eav
  "Take keyword as entity and collection as args and return args as is"
  [e & args]
  (assoc {} e (first args)))

(defn eav2
  "Take keyword as entity and collection as args and return args as is"
  [e & args]
  (assoc {} e (reduce conj {} (first args))))

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
    {:asn                    #(assoc {} :<asn>  (clojure.edn/read-string %))
     :remote-as              #(conj [] :remote-as (clojure.edn/read-string %))
     :identifier             #(conj [] :identifier (clojure.edn/read-string %))
     :ip_address             (comp #(conj [] :ip_address %) str)
     :name                   (comp #(conj [] :<name> %) str)
     :peers                  (fn peers [& arg]
                               (conj [] :peers (conj (into (sorted-set) arg))))
     :bgplist                (fn m [& args] (eavf :<bgplist> args))
     :neighbors              (fn m [& args] (eavf :<neighbors> args))
     :router_bgp             (fn m [& args] (eavf :router_bgp args))
     :device                 (fn m [& args] (eavf :<device> args))
     :synchronization        (fn m [& args] (chtobool :synchronization args))
     :always-compare-med     (fn m [& args] (chtobool :always-compare-med args))
     :deterministic-med      (fn m [& args] (chtobool :deterministic-med args))
     :compare-routerid       (fn m [& args] (chtobool :compare-routerid args))
     :as-path_confed         (fn m [& args] (chtobool :as-path_confed args))
     :next-hop-self          (fn m [& args] (chtobool :next-hop-self args))
     :interface              (fn m [& args] (eav2 :interface args))
     :neighbor               (fn m [& args] (eav2 :neighbor args))
     :interfaces             (fn m [& args] (eavf :<interfaces> args))
     :map                    (fn m [& args] (av args))
     :naddr                  (fn m [& args] (av args))
     :hostname               (comp #(assoc {} :hostname %))}
    input))
  


    

