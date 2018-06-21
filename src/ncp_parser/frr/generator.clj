(ns ncp-parser.frr.generator
  (:require [com.rpl.specter :refer :all]
            [com.rpl.specter.zipper :as SZ]
            [clojure.zip :as zip]
            [clojure.walk :refer [postwalk prewalk]]
            [clojure.string :as str]
            [spyscope.core :refer :all]))

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

; (defn transpose-tag [tag]
;  (str/join \space (str/split tag #"_")))

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
        (println "adding keyword -->> " tag)
        (swap! container conj tag)))))

(defn vector-handler! [node]
  (condp #(= (first %2) %1) node
    :ip_address (println "found ip_address tag --> " (rest node))))

(defn set-handler! [node]
  (println "adding set -->> " node)
  (swap! container conj (str (str/join \space node) \newline)))

(defn string-handler! [node]
  (println "adding string -->> " node)
  (swap! container conj (str node \newline)))

(defn number-handler! [node]
  (println "adding number -->> " node)
  (swap! container conj (str node \newline)))

(defn regex-handler! [node]
  (println "adding regex -->> " node)
  (swap! container conj (str node \newline))
  "")

(defn vector-handler! [node]
  (println "executing vector-handler " node)
  (if (and (not (map? (first node)))
           (= (count node) 2))
    (let [[option value] node
          option-string (name option)]
      (if (is-options-tag? option-string)
        (let [fmt-option (subs (transpose-tag option-string) 1)]
          (println "adding option -->> " fmt-option value)
          (cond
            (boolean? value) (condp = value
                               true (do (swap! container conj (str fmt-option \newline)) node)
                               false (do (swap! container conj (str "no " fmt-option \newline)) node))
            (number? value) (do (swap! container conj (str fmt-option value \newline)) [nil nil])))
        node))
    node))

(defn map-handler! [node]
  (println "executing map-handler " node)
  ; Handle map options differently as they are variadic.
  (if
    (is-option-map? node)
    (do
      (println "Processing options map " node)
      (let [result (reduce
                     (fn [acc m]
                       (let [[option value] m
                             _ (println "Processing option value " option value)
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
  (println "Working on node ->" node)
  (cond
    (is-regex? node) (regex-handler! node)
    (string? node) (do (string-handler! node) node)
    (number? node) (do (number-handler! node) node)
    (set? node) (do (set-handler! node) (println "NODE-->> " node) #{})
    (keyword? node) (do (keyword-handler! node) node)
    (vector? node) (vector-handler! node)
    (map? node) (map-handler! node)
    :else node))

(def model
  {:<device>
   {:hostname            "J"
    :<interfaces>
                         [{:interface {:<name> "eth0", :ip_address "10.0.18.1/24"}}
                          {:interface {:<name> "eth1", :ip_address "10.0.19.1/24"}}
                          {:interface {:<name> "eth2", :ip_address "10.0.15.2/24"}}
                          {:interface {:<name> "eth3", :ip_address "10.0.13.2/24"}}
                          {:interface {:<name> "eth4", :ip_address "10.0.11.2/24"}}
                          {:interface {:<name> "eth5", :ip_address "10.0.9.2/24"}}]
    :router_bgp          {:<asn>            65525,
                          :+synchronization false
                          :<bgplist>
                                            [{:bgp {:router-id "192.0.0.1",}}
                                             {:bgp {:+always-compare-med true}}
                                             {:bgp {:+deterministic-med true}}
                                             {:bgp {:bestpath {:+compare-routerid true}}}
                                             {:bgp {:bestpath {:+as-path_confed true}}}
                                             {:bgp {:confederation {:identifier 100}}}
                                             {:bgp {:confederation {:peers #{65529 65528 65527 65530},}}}]
                          :<neighbors>
                                            [{:neighbor {:10.0.18.2 {:remote-as 200}}}
                                             {:neighbor {:10.0.19.2 {:remote-as 300}}}
                                             {:neighbor {:10.0.15.1 {:remote-as 65527}}}
                                             {:neighbor {:10.0.15.1 {:+next-hop-self true}}}
                                             {:neighbor {:10.0.15.1 {:send_community "both"}}}
                                             {:neighbor {:10.0.15.1 {:advertisement-interval "5"}}}
                                             {:neighbor {:10.0.13.1 {:remote-as 65528}}}
                                             {:neighbor {:10.0.13.1 {:+next-hop-self true}}}
                                             {:neighbor {:10.0.13.1 {:send-community "both"}}}
                                             {:neighbor {:10.0.13.1 {:advertisement-interval "5"}}}
                                             {:neighbor {:10.0.18.2 {:route-map {:rm-in "in"}}}}
                                             {:neighbor {:10.0.18.2 {:route-map {:rm-export-2 "out"}}}}]}
    :<ip-prefix-list>    [{:ip_prefix-list {:pl-1 {:permit {:<prefix> "1.0.0.0/24"}}}}
                          {:ip_prefix-list {:pl-2 {:deny {:<prefix> "1.0.0.0/24"}}}}
                          {:ip_prefix-list {:pl-3 {:permit {:<prefix> "1.0.1.0/24"}}}}
                          {:ip_prefix-list {:pl-4 {:deny {:<prefix> "1.0.1.0/24"}}}}
                          {:ip_prefix-list {:pl-5 {:permit {:<prefix> "2.0.0.0/24"}}}}
                          {:ip_prefix-list {:pl-6 {:deny {:<prefix> "2.0.0.0/24"}}}}
                          {:ip_prefix-list {:pl-7 {:permit {:<prefix> "2.0.1.0/24"}}}}
                          {:ip_prefix-list {:pl-8 {:deny {:<prefix> "2.0.1.0/24"}}}}
                          {:ip_prefix-list {:pl-9 {:permit {:+<prefix> "0.0.0.0/0" :le 32}}}}
                          {:ip_prefix-list {:pl-10 {:deny {:+<prefix> "0.0.0.0/0" :le 32}}}}]

    :<ip-community-list> [{:ip_community-list_standard {:cl-1 {:permit "200:1"}}}
                          {:ip_community-list_standard {:cl-2 {:permit "200:2"}}}
                          {:ip_community-list_standard {:cl-3 {:permit "100:1"}}}
                          {:ip_community-list_standard {:cl-4 {:permit "100:2"}}}
                          {:ip_community-list_standard {:cl-5 {:permit "100:3"}}}
                          {:ip_community-list_standard {:cl-6 {:permit "100:4"}}}
                          {:ip_community-list_standard {:LOCAL {:permit #{"100:1" "100:2" "100:3" "100:4"}}}}]
    :<ip-as-path-list>   [{:ip_as-path_access-list {:path-1 {:permit #"^\(?(65527|65528|65529|65530)_"}}}
                          {:ip_as-path_access-list {:path-3 {:permit #"^\(?300_}"}}}
                          {:ip_as-path_access-list {:path-4 {:permit #"^\(?200_"}}}]

    :<route-map-list>    [{:route-map            {:rm-in {:permit 10}}
                           :match_ip_address     {:prefix-list "pl-1"}
                           :match_as-path        "path-1"
                           :set_community        {:+<community> "100:1" :+additive true}}
                          {:route-map            {:rm-in {:permit 120}}
                           :match_community      "cl-2"
                           :match_ip_address     {:prefix-list "pl-9"}
                           :match_as-path        "path-1"
                           :set_local-preference 99
                           :set_community        {:+<community> "100:1" :+additive true}}]}})



(defn output_config []
  (prewalk gen-config model)
  (print (str/join @container)))
