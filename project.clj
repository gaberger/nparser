(defproject ncp-parser "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "https://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [instaparse "1.4.8"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [expound "0.6.0"]
                 [com.velisco/strgen "0.1.7"]
                 [org.clojure/test.check  "0.10.0-alpha2"]
                 [com.rpl/specter  "1.1.1"]
                 [org.clojure/tools.cli  "0.3.7"]
                 [eftest  "0.5.2"]]
  :main ncp-parser.frr.core
  :aot [ncp-parser.frr.core])
