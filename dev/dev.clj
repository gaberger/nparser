(ns dev
  (:require
   [ncp-parser.utils :refer [get-grammar-file get-config-file]]
   [ncp-parser.frr.generator :refer [gen-config container transpose-tag]]
   [clojure.walk :refer [postwalk prewalk]]
   [clojure.string :as str]
   [clojure.pprint :refer [pprint]]
   [clojure.reflect :refer [reflect]]
   [clojure.repl :refer [apropos dir doc find-doc pst source]]
   [clojure.test :refer [run-all-tests]]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [instaparse.core :as insta :refer [parser defparser transform]]
   [spyscope.core :refer :all]))

; (in-ns `ncp-parser.frr.generator)

; (defn test-all []
;   (run-all-tests #"edge.*test$"))



(def whitespace
    (insta/parser
      "whitespace = #'\\s+'"))

(def whitespace-or-comments
    (insta/parser
      "ws-or-comment = #'\\s+' | comment
	     comment = '!'"
     :auto-whitespace whitespace))

(defonce grammar (get-grammar-file "parsers/frr/frr-new.ebnf"))
(def config (get-config-file "configs/frr/router1-test.cfg"))

(defparser frr grammar :output-format :hiccup :string-ci true :auto-whitespace whitespace-or-comments)

(defn t [args]
  (let [[address options] args
        newkey (keyword (str address))
        newmap #spy/p options]
      (assoc {} newkey newmap)))

  ; (let [k (first args)]
  ;   (conj {} :test (reduce conj {} (rest args)))))

 ; "[:map "rm-in" "in"]]")
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

(defn gen [input]
  (prewalk gen-config input))

(defn chtobool [k args]
  (let [attr (str/replace (first args) #" " "_")
        kton (name k)
        ntok (keyword (str "+"kton))]
    (cond
       (= attr "no") (assoc {} ntok false)
       (= attr kton) (assoc {} ntok true)
      :default args)))

(defn frr-transform [input]
  (transform
    {  :asn                      (fn asn [arg]
                                     (assoc {} :<asn> (clojure.edn/read-string arg)))
       :ip_address               (comp #(conj [] :ip_address %) str)
       :name                     (comp #(conj [] :<name> %) str)
       :interface               (fn interfaces [& arg]
                                  (assoc {} :interface
                                    (reduce conj {} arg)))
       :bgplist                (fn bgplist [& arg] (assoc {} :<bgplist> (into [] arg)))
       :bestpath               (fn bestpath [& arg]
                                  (assoc {} :bestpath
                                    (reduce conj {} arg)))
       :synchronization        (fn sync [& args]
                                 (chtobool :synchronization args))
       :always-compare-med     (fn sync [& args]
                                  (chtobool :always-compare-med args))
       :deterministic-med     (fn sync [& args]
                                  (chtobool :deterministic-med args))
       :compare-routerid      (fn sync [& args]
                                  (chtobool :compare-routerid args))
       :as-path_confed        (fn sync [& args]
                                  (chtobool :as-path_confed args))
       :map                    (fn m [& args] (av args))
       :route-map               #(assoc {} :routemap %)
       :remote-as               #(assoc {} :remote-as %)
       :send-community          #(assoc {} :send-community %)
       :advertisement-interval  #(assoc {} :advertisement-interval %)
       :next-hop-self           #(assoc {} :+next-hop-self true)
       :naddr                    (fn m [& args] (av args))
       :hostname                 (fn m [& args] (eav :hostname args))
       :router_bgp               (fn m [& args] (eavl :router_bgp args))
       :neighbor                 (fn m [& args] (eav :neighbor args))
       :neighbors                (fn m [& args] (eavl :<neighbors> args))
       :interfaces               (fn m [& args] (eavl :<interfaces> args))
       :device                   (fn m [& args] (eavl :<device> args))
       ; :map                    (fn m [& arg]
       ;                            (let [attrs (into [] (keyword (first arg)) next)]
       ;                              (assoc {} :map attrs)))

       ; :route-map               (fn bestpath [& arg]
       ;                            (assoc {} :route-map
       ;                              (reduce conj {} arg)))
       ; :naddr                  (fn naddr [& arg]
       ;                              (t arg))

       :bgp                    (fn bgp [& arg]
                                      (assoc {} :bgp
                                        (reduce conj {} arg)))
       ; :neighbor               (fn neighbor [& arg]
       ;                                    (assoc {} :neighbor
       ;                                      (reduce conj {} arg)))
       :peers              (fn peers [& arg]
                                     (conj [] :peers (into #{} (map #(clojure.edn/read-string %) arg))))}


   input))
