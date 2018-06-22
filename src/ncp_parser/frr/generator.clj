(ns ncp-parser.frr.generator
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.zipper :as SZ]
            [clojure.zip :as zip]
            [clojure.walk :refer [prewalk]]
            [clojure.string :as str]
            [taoensso.timbre :as timbre
             :refer [log  trace  debug  info  warn  error  fatal  report
                     logf tracef debugf infof warnf errorf fatalf reportf
                     spy get-env]]
            [taoensso.timbre.appenders.core :as appenders]))

(set! *warn-on-reflection* 1)

(timbre/refer-timbre)
(defonce logfile *ns*)
(timbre/merge-config! {:level :debug
                       :appenders {:println {:enabled? true}}})
;(timbre/merge-config!
;  {:appenders {:spit (appenders/spit-appender {:fname (str/join [logfile ".log"])})}})

(defonce container (atom []))

(def key-regex #"^(\<[a-zA-Z-_]+\>|\+[a-zA-Z-_]+)$")
(def options-regex #"^\+")

(defn is-skip-tag? [tag]
  (some? (re-matches key-regex tag)))

(defn is-transpose-tag? [tag]
  (some? (re-find #"_" tag)))

(defn is-options-tag? [tag]
  (some? (re-find options-regex tag)))

(defn is-option-map? [m]
  (and (> (count m) 1)
       (some? (re-find options-regex (name (key (first m)))))))

(defn is-regex? [tag]
  (instance? java.util.regex.Pattern tag))

(defn is-regular-tag? [tag]
  (and (not (is-skip-tag? tag))
       (not (is-transpose-tag? tag))
       (not (is-options-tag? tag))))

(defn transpose-tag [tag]
  (str (str/join \space (str/split tag #"_")) \space))

(defn skip-tag [tag]
  (when-not (is-skip-tag? tag)
    (str tag)))

(defn regular-tag [tag]
  (let [tag-string (str tag \space)]
    (swap! container conj tag-string)))

(defn keyword-handler! [tag]
  (let [tag-key (name tag)]
    (if-let [tag (some->> tag-key skip-tag transpose-tag)]
      (do
        (debug "adding keyword -->> " tag)
        (swap! container conj tag)))))

(defn vector-handler! [node]
  (condp #(= (first %2) %1) node
    :ip_address (println "found ip_address tag --> " (rest node))))

(defn set-handler! [node]
  (debug "adding set -->> " node)
  (swap! container conj (str (str/join \space node) \newline)))

(defn string-handler! [node]
  (debug "adding string -->> " node)
  (swap! container conj (str node \newline)))

(defn number-handler! [node]
  (debug "adding number -->> " node)
  (swap! container conj (str node \newline)))

(defn regex-handler! [node]
  (debug "adding regex -->> " node)
  (swap! container conj (str node \newline))
  "")

(defn vector-handler! [node]
  (debug "executing vector-handler " node)
  (if (and (not (map? (first node)))
           (= (count node) 2))
    (let [[option value] node
          option-string (name option)]
      (if (is-options-tag? option-string)
        (let [fmt-option (subs (transpose-tag option-string) 1)]
          (debug "adding option -->> " fmt-option value)
          (cond
            (boolean? value) (condp = value
                               true (do (swap! container conj (str fmt-option \newline)) node)
                               false (do (swap! container conj (str "no " fmt-option \newline)) node))
            (number? value) (do (swap! container conj (str fmt-option value \newline)) [nil nil])))
        node))
    node))

(defn map-handler! [node]
  (debug "executing map-handler " node)
  ; Handle map options differently as they are variadic.
  (if
    (is-option-map? node)
    (do
      (debug "Processing options map " node)
      (let [result (reduce
                     (fn [acc m]
                       (let [[option value] m
                             _ (debug "Processing option value " option value)
                             option-string (name option)
                             strip-tag (if (is-options-tag? option-string)
                                         (some->> (subs option-string 1)
                                                  skip-tag transpose-tag)
                                         option-string)]
                         (if (nil? strip-tag)
                           (conj acc (str value))
                           (cond
                             (boolean? value) (condp = value
                                                true (conj acc (str strip-tag))
                                                false (conj acc (str "no " strip-tag)))
                             (number? value) (conj acc (str strip-tag \space value))))))
                     []
                     node)
            retval (str/join \space result)]
        (swap! container conj (str retval \newline)))
      (empty {}))
    node))

(defn gen-config [node]
  (cond
    (is-regex? node) (regex-handler! node)
    (string? node) (do (string-handler! node) node)
    (number? node) (do (number-handler! node) node)
    (set? node) (set-handler! node)
    (keyword? node) (do (keyword-handler! node) node)
    (vector? node) (vector-handler! node)
    (map? node) (map-handler! node)
    :else node))



(defn generator [input]
  (prewalk gen-config input))

;(defn output_config []
;  (prewalk gen-config model)
;  (print (str/join @container)))
