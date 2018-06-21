(ns ncp-parser.utils
  (:require [org.httpkit.client :as http]
            [clojure.string :as str]
            [cheshire.core :refer :all]
            [clojure.tools.logging :refer [info]]))

;
(defn list-github-path [owner repo path]
  (let [resource "contents"
        api-base "https://api.github.com/repos"
        file-base (str/join "/" [owner repo])
        uri (str/join "/" [api-base owner repo resource path])
        _ (info "Making request to " uri)]
    (let [{:keys [status headers body error] :as resp} @(http/get uri)]
      (if error
        false
        (let [edn (into [] (parse-string body true))
              acc []]
          (reduce
            (fn [acc file]
              (conj acc {:path (:path file) :repo repo :owner owner}))
            []
            edn))))))

(defn get-github-file [branch m]
  (let [{:keys [path repo owner]} m
        api-base "https://raw.githubusercontent.com"
        uri (str/join "/" [api-base owner repo branch path])
        _ (info "Making request to " uri)]
    (let [{:keys [status headers body error] :as resp} @(http/get uri)]
      (if error
        false
        body))))




(defn get-grammar-file [file]
  ;"//Volumes/jamastore1/WIP/ncp_parser/parsers/frr/frr.ebnf")
  (->> (clojure.java.io/as-file file)  slurp))

(defn get-config-file [file]
  (->> (clojure.java.io/as-file file)  slurp))
