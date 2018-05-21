(ns ncp-parser.frr.core
 (:require [instaparse.core :as insta :refer [defparser]]
           [clojure.pprint :as pprint :refer [pprint]]
           [clojure.java.io :as io]
           [clojure.string :as str]
           [clojure.spec.alpha :as s]
           [eftest.runner :refer [find-tests run-tests]]))

(def whitespace
    (insta/parser
      "whitespace = #'\\s+'"))

(def whitespace-or-comments
    (insta/parser
      "ws-or-comment = #'\\s+' | comment
	     comment = '!'"
     :auto-whitespace whitespace))

(defn create-frr-parser []
    (let [grammar (io/resource "parsers/frr/frr.ebnf")]
      (defparser  frr
         grammar
        :output-format :hiccup
        :string-ci true
        :auto-whitespace whitespace-or-comments)))


(defmacro create-parser [device]
    (let [grammar-file (str/join "." [device "ebnf"])
          grammar (io/resource (str/join "/" ["parsers" grammar-file]))]
      `(defparser  ~(symbol device)
         ~grammar
        :output-format :hiccup
        :string-ci true
        :auto-whitespace whitespace-or-comments)))

(defn debug-transform [& arg]
  (println "---------------------")
  ; (pprint arg)
  (println (count arg))
  (println (type arg))
  arg)

  ; (reduce conj [] arg))

(defn frr-transform [input]
  (insta/transform
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

(defn -main []
   (create-frr-parser)
   (pprint (->> (frr (slurp (io/resource "configs/frr/router1.cfg"))) frr-transform)))
