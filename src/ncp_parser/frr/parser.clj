(ns ncp-parser.frr.parser
    (:require  [clojure.java.io :as io]
               [clojure.string :as str]
               [clojure.pprint :refer [pprint]]
               [instaparse.core :as insta :refer [defparser]]
               [ncp-parser.frr.transform :refer [frr-transform]]))


(def whitespace
    (insta/parser
      "whitespace = #'\\s+'"))

(def whitespace-or-comments
    (insta/parser
      "ws-or-comment = #'\\s+' | comment
	     comment = '!'"
     :auto-whitespace whitespace))

(defn create-frr-parser [grammar]
      (defparser  frr
         grammar
        :output-format :hiccup
        :string-ci true
        :auto-whitespace whitespace-or-comments))


; (defmacro create-parser [device]
;     (let [grammar-file (str/join "." [device "ebnf"])
;           grammar (io/resource (str/join "/" ["parsers" grammar-file]))]
;       `(defparser  ~(symbol device)
;          ~grammar
;         :output-format :hiccup
;         :string-ci true
;         :auto-whitespace whitespace-or-comments)))

(defn debug-transform [& arg]
  (println "---------------------")
  ; (pprint arg)
  (println (count arg))
  (println (type arg))
  arg)

(defn parse-frr [grammar config]
   (create-frr-parser grammar)
   (frr-transform (frr config)))
