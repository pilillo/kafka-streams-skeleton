#!/usr/bin/env bash
# run directly the process
docker run -t --env USERNAME=user --env PASSWORD=pwd --env BROKERHOST=broker01.realm --env TOPIC=topiconkafka --dns=xyz.xyz.xzy.xyz --dns-search=realm pilillo/kafka-streams-skeleton:1.0

# run as interactive
#docker run -i --env USERNAME=user --env PASSWORD=pwd --env BROKERHOST=broker01.realm --env TOPIC=topiconkafka --dns=xyz.xyz.xyz.xyz --dns-search=realm -t pilillo/kafka-streams-skeleton:1.0 bash
