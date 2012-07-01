(defproject play-topologies "0.0.1-SNAPSHOT"
  :source-path "src"
  :javac-options {:debug "true" :fork "true"}
  :jvm-opts ["-Djava.library.path=/usr/local/lib:/opt/local/lib:/usr/lib"]
  :aot :all
  :dependencies [[org.clojure/clojure "1.3.0"]
  				[storm "0.7.4"]]
  )