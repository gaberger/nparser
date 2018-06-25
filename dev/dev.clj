(ns dev
  (:require
    [clojure.repl :refer [apropos dir doc find-doc pst source]]
    [clojure.tools.namespace.repl :refer [refresh refresh-all]]
    [nparser.frr.parser :refer [create-frr-parser]]
    [clojure.pprint :refer [pprint]]
    [nparser.frr.generator :refer [generator] :as g]
    [nparser.utils :refer :all])) 

