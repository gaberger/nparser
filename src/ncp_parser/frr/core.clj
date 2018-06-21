(ns ncp-parser.frr.core
 (:require  [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [eftest.runner :refer [find-tests run-tests]])
 (:gen-class))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(def cli-options
  [["-h" "--help" "p"]])


(defn usage [options-summary]
  (->> ["Usage: ncp-parser.frr.core [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  parse     Parse example frr router config"
        ""
        "Please refer to the manual page for more information."]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) {:exit-message (usage summary) :ok? true}
      errors {:exit-message (error-msg errors)}
      (and (= 1 (count arguments))
           (#{"parse"} (first arguments)))
      {:action (first arguments) :options options}
      :else {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg))

;(defn -main [& args]
;  (let [{:keys [action options exit-message ok?]} (validate-args args)]
;    (if exit-message
;      (exit (if ok? 0 1) exit-message)
;      (case action
;        "parse" (parse-frr)))))
