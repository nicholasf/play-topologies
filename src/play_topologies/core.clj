(ns play-topologies.core
	(:import [backtype.storm StormSubmitter LocalCluster])
	(:use [backtype.storm clojure config log])
	(:gen-class))

(defspout clock-spout ["tick tock"]
	[conf context collector]
	(spout 
		(nextTuple []
			(Thread/sleep 1000)
			(print "\n\n ---- clock-spout ready to emit current second ...")
			;(emit-spout! collector [(System/currentTimeMillis)])
			(emit-spout! collector [(System/currentTimeMillis)])
			(log-message "test log message each second")
		)
		(ack [id]
			;;reliable spout
		)))

(defbolt second-logger ["second" "logger"] {:prepare true}
	[conf context collector]
	(bolt
		(execute [tuple]
			(let [millis  (.getLong tuple 0)]
				(print millis)
				(log-message millis)	
			)
			)))
(defn mk-topology []
	(topology
		{"1" (spout-spec clock-spout)}
		{"2" (bolt-spec {"1" :shuffle} second-logger :p 2)}
	))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "time-topology" {TOPOLOGY-DEBUG true} (mk-topology))
;    (Thread/sleep 10000)
;    (.shutdown cluster)
    ))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))

