#!/usr/bin/env bash
sshpass -p "$PASSWORD" ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null -L 8081:$BROKERHOST:8081 -L 9091:$BROKERHOST:9091 -L 2181:$BROKERHOST:2181 -fN $USERNAME@$BROKERHOST
java -jar /kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar --name Gilberto --zookeeper localhost:2181 --broker localhost:9091 --schema-registry http://localhost:8081 --from-beginning --topic $TOPIC
