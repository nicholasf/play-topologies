(ns play-topologies.core
	(:import [backtype.storm StormSubmitter LocalCluster])
	(:use [backtype.storm clojure config])
	(:gen-class))

(defspout clock-spout ["tick tock"]
	[conf context collector]
	(spout 
		(nextTuple []
			(Thread/sleep 1000)
			(emit-spout! collector [(System/currentTimeMillis)])
			)
		(ack [id]
			;;reliable spout
		)))

(defbolt second-logger ["second" "logger"] {:prepare true}
	[conf context collector]
	(bolt
		(execute [tuple]
			(let [millis  (.getString tuple 0)]
				(print "****")
				(print millis)
				))))
(defn mk-topology []
	(topology
		{"1" (spout-spec clock-spout)}

		{"2" (bolt-spec {"1" :shuffle} second-logger :p 2)}
	))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "time-topology" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)
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
