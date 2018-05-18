(ns ncp-parser.core
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


(defmacro create-parser [device]
    (let [grammar-file (str/join "." [device "ebnf"])
          grammar (slurp (io/resource (str/join "/" ["parsers" grammar-file])))]
      `(defparser  ~(symbol device)
         ~grammar
        :output-format :hiccup
        :string-ci true
        :auto-whitespace whitespace-or-comments)))



(defn bgp-transform [input]
  (insta/transform
    {
        :bgprouter                 (fn bgprouter [& arg]
                                     (assoc {} :bgprouter (reduce conj {} arg)))
        :asn                       (fn asn [arg]
                                      (let [asnval (clojure.edn/read-string arg)]
                                       {:asn asnval}))
        :bgp-global                (fn c [& arg]
                                     (assoc {} :bgp-global (reduce conj {} arg)))
        :bgp-med                   (fn c [& arg]
                                     (assoc {} :bgp-med (into [] arg)))
        :bgp-bestpath               (fn c [& arg]
                                     (assoc {} :bgp-bestpath (into [] arg)))
        :bgp-confederation          (fn c [& arg]
                                      (assoc {} :bgp-confederation (reduce conj {} arg)))
        :confederation-peers        (fn cp [& arg]
                                      (conj [] :confederation-peers (into #{} (map #(clojure.edn/read-string %) arg))))
        :confederation-identifier   (fn ci [arg]
                                       {:confederation-identifier  (clojure.edn/read-string arg)})}
   input))
