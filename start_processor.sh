#!/usr/bin/env bash

#java -cp target/kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.github.pilillo.KStreamProcessor
#java -cp target/kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.github.pilillo.KStreamProcessor

java -jar target/kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar --name Gilberto --zookeeper localhost:2181 --broker localhost:9091 --schema-registry http://localhost:8081 --from-beginning 
