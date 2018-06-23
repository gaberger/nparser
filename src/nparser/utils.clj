(ns nparser.utils
  (:require [org.httpkit.client :as http]
            [clojure.string :as str]
            [cheshire.core :refer :all]
            [yaml.core :as yaml :exlude [load]]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :as appenders]))

(set! *warn-on-reflection* 1)

(timbre/refer-timbre)

;
(defn list-github-path [owner repo path]
  (let [resource "contents"
        api-base "https://api.github.com/repos"
        file-base (str/join "/" [owner repo])
        uri (str/join "/" [api-base owner repo resource path])
        _ (debug "Making request to " uri)]
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
        _ (debug "Making request to " uri)]
    (let [{:keys [status headers body error] :as resp} @(http/get uri)]
      (if error
        false
        body))))


(defn get-file [file]
  (->> (clojure.java.io/as-file file) slurp))

(defn edn->yaml [input]
  (yaml/generate-string input))

(defn yaml->edn [input]
  (yaml/parse-string input))

(defn get-yaml-config [file]
  (yaml/parse-string (get-file file)))
