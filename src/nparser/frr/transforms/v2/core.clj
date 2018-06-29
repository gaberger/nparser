(ns nparser.frr.transforms.v2.core
  (:require [clojure.string :as str]
            [instaparse.core :as insta :refer [transform]]))

(defn av
  [args]
  (let [[a v] args]
    (assoc {} (keyword a) v)))

(defn eavl
  "Take keyword as entity and collection as args and return args as vector to key"
  [e args]
  (assoc {} e (into []  args)))

(defn eavt
  "Take keyword as entity and text element as args"
  [e args]
  (assoc {} e args))

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
     :identifier             (fn asn [arg]
                               (assoc {} :identifier (clojure.edn/read-string arg)))
     :remote-as              (fn asn [arg]
                               (assoc {} :remote-as (clojure.edn/read-string arg)))   
     :response-lifetime      (fn asn [arg]
                               (assoc {} :response-lifetime (clojure.edn/read-string arg)))
     :ip_address             (comp #(conj [] :ip_address  %) str)
     :description            (fn e [& arg] (eavt :description  (pr-str (str/join #" " (into [] arg)))))
     :name                   (comp #(conj [] :<name> %) str)
     :interface              (fn interfaces [& arg]
                               (assoc {} :interface
                                         (reduce conj {} arg)))    
     
     :bgplist                (fn bgplist [& arg] (assoc {} :<bgplist> (into [] arg)))
     :bgp                    (fn bgp [& arg]
                               (assoc {} :bgp
                                         (reduce conj {} arg)))
     :bestpath               (fn bestpath [& arg]
                               (assoc {} :bestpath
                                         (reduce conj {} arg)))
     ; :synchronization        (fn sync [& args] (chtobool :synchronization args))
     :always-compare-med     (fn sync [& args] (chtobool :always-compare-med args))
     :deterministic-med      (fn sync [& args] (chtobool :deterministic-med args))
     :compare-routerid       (fn sync [& args] (chtobool :compare-routerid args))
     :as-path_confed         (fn sync [& args] (chtobool :as-path_confed args))
     :peers                  (fn peers [& arg]
                               (assoc {} :peers (conj (into (sorted-set) arg))))
     ; :map                    (fn m [& args] (av args))
     ; :route-map              #(assoc {} :route-map %)
     ; :remote-as              #(assoc {} :remote-as (clojure.edn/read-string %))
     ; :send-community         #(assoc {} :send-community %)
     ; :advertisement-interval #(assoc {} :advertisement-interval %)
     ; :next-hop-self          (fn b [& args] (chtobool :next-hop-self args))
     :naddr                  (fn m [& args] (av args))
     :npeer                  (fn m [& args] (av args))
     :afnpeer                (fn m [& args] (av args))
     :line                   (fn m [arg] (eavt :line arg))
     :peer-group             (fn m [arg] (eavt :peer-group arg))
     :update-source          (fn m [arg] (eavt :update-source arg))
     :address-family         (fn m [arg] (eavt :address-family arg))
     :router-id              (fn m [arg] (eavt :router-id arg))
     :service                (fn m [arg] (eavt :service arg))
     :frrhead                (fn m [& args] (eavl :<frrhead> args))
     :frr                    (fn m [args] (eavt :<frr> args))
     :frrdefaults            (fn m [arg] (eavt :<frrdefaults> arg))
     :exitvnc                (fn m [arg] (eavt :<exitvnc> arg))
     :exit-address-family    (fn m [arg] (eavt :<exit-address-family> arg))
     :vncdefaults            (fn m [arg] (eavt :<vncdefaults> arg))
     :hostname               (fn m [& args] (eav :hostname args))
     :router_bgp             (fn m [& args] (eavl :router_bgp args))
     :afiu                   (fn m [& args] (eavl :<afiu> args))
     :afneighbor             (fn m [& args] (eavt :neighbor (first args)))
     :afneighbors            (fn m [& args] (eavl :<afneighbors> args))
     :neighbors              (fn m [& args] (eavl :<neighbors> args))
     :neighbor               (fn s [& arg]
                                  (assoc {} :neighbor (reduce conj {} arg)))
     :interfaces             (fn m [& args] (eavl :<interfaces> args))
     :vnc                    (fn m [& args] (eavl :vnc args))
     :device                 (fn m [& args] (assoc {} :<device> (reduce conj {} args)))}
    input))


