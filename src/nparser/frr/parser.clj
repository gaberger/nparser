(ns nparser.frr.parser
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [instaparse.core :as insta :refer [parser]]))


(def whitespace
  (insta/parser
    "whitespace = #'\\s+'"))

(def whitespace-or-comments
  (insta/parser
    "ws-or-comment = #'\\s+' | comment
     comment = '!'"
    :auto-whitespace whitespace))

(defn create-frr-parser [grammar]
  (parser
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


