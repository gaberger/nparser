(ns nparser.frr.transforms.v2.core
  (:require [clojure.string :as str]
            [instaparse.core :as insta :refer [transform]]))

(defn av
  [args]
  (let [[a v] args]
    (conj [] (keyword a) v)))

(defn eav
  "Take keyword as entity and collection as args and return args as is"
  [e & args]
  (conj [] e (into [] (first args))))

(defn chtobool [k args]
  (let [attr (str/replace (first args) #" " "_")
        kton (name k)
        ntok (keyword (str "+" kton))]
    (cond
      (= attr "no") (conj [] ntok false)
      (= attr kton) (conj [] ntok true)
      :default args)))

(defn transformer [input]
  (transform
    {:asn                    #(conj [] :<asn> (clojure.edn/read-string %))
     :remote-as              #(conj [] :remote-as (clojure.edn/read-string %))
     :identifier             #(conj [] :identifier (clojure.edn/read-string %))
     :ip_address             (comp #(conj [] :ip_address %) str)
     :name                   (comp #(conj [] :<name> %) str)
     :peers                  (fn peers [& arg]
                               (conj [] :peers (conj (into (sorted-set) arg))))
     :bgplist                (fn m [& args] (eav :<bgplist> args))
     :neighbors              (fn m [& args] (eav :<neighbors> args))
     :interfaces             (fn m [& args] (eav :<interfaces> args))
     :device                 (fn m [& args] (eav :<device> args))
     :synchronization        (fn m [& args] (chtobool :synchronization args))
     :always-compare-med     (fn m [& args] (chtobool :always-compare-med args))
     :deterministic-med      (fn m [& args] (chtobool :deterministic-med args))
     :compare-routerid       (fn m [& args] (chtobool :compare-routerid args))
     :as-path_confed         (fn m [& args] (chtobool :as-path_confed args))
     :next-hop-self          (fn m [& args] (chtobool :next-hop-self args))
     :map                    (fn m [& args] (av args))
     :naddr                  (fn m [& args] (av args))}
    input))



    

