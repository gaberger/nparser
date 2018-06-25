(ns nparser.frr.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [cli-matic.core :refer :all]
            [nparser.frr.generator :refer [container] :as g]
            [nparser.frr.parser :refer :all]
            [nparser.utils :refer :all]
            [nparser.frr.transforms.v1.core :refer [transformer]]
            [taoensso.timbre :as timbre]
            [yaml.core :as yaml]
            [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]
            [taoensso.timbre.appenders.core :as appenders])
  (:gen-class))

(set! *warn-on-reflection* 1)

;(timbre/refer-timbre)
;(defonce logfile *ns*)
;(timbre/merge-config! {:appenders {:println {:enabled? true}}})
;(timbre/merge-config!
;  {:appenders {:spit (appenders/spit-appender {:fname (str/join [logfile ".log"])})}})


(defn gen-config [arg]
  (let [inputfile (:file arg)
        e (-> (get-file inputfile) (json/parse-string true))
        _ (pprint e)
        c (g/generator e)
        _ (println  c)]))
    ; (print (str/join c))))

(defn gen-yaml [arg]
  (let [configuration (get-file (:file arg))
        grammar (get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        z (yaml/generate-string t :dumper-options {:flow-style :block})]
      (println (str z))))
        ; (yaml/generate-string t)))

(defn gen-json [arg]
  (let [configuration (get-file (:file arg))
        grammar (get-file "./parsers/frr/frr-new.ebnf")
        parser (create-frr-parser grammar)
        t (transformer (parser configuration))
        z (json/generate-string t)]
      (println (str z))))
        ; (yaml/generate-string t)))


(def CONFIGURATION
  {:app         {:command     "nparser"
                 :description "A command-line configuration generator"
                 :version     "0.0.1"}

   :global-opts []

   :commands    [ 
                  ; {:command     "gen-config"}
                  ; :description "Generate a config from a YAML input file"
                  ; :opts        [{:option "file" :as "YAML input file" :type :string}]
                  ; :runs        gen-config

                 {:command     "to-yaml"
                  :description "Generate a YAML file from a config"
                  :opts        [{:option "file" :as "Config input file" :type :string}]
                  :runs        gen-yaml}        
                 {:command     "to-json"
                  :description "Generate JSON from a config"
                  :opts        [{:option "file" :as "Config input file" :type :string}]
                  :runs        gen-json}    
                 {:command     "to-config"
                  :description "Generate config from an input file"
                  :opts        [{:option "file" :as "JSON input file" :type :string}]
                  :runs        gen-config}]})


(defn -main [& args]
  (run-cmd args CONFIGURATION))
