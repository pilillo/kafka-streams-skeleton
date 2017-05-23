FROM openjdk:8-jre
MAINTAINER andrea.monacchi@gmail.com

COPY target/kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar /kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar

RUN apt-get update && \
    apt-get install sshpass -y --force-yes && \
    apt-get install telnet && \
    chmod +x /kafka-streams-processor-0.0.1-SNAPSHOT-jar-with-dependencies.jar

COPY startup.sh /startup.sh
RUN chmod +x /startup.sh

CMD ["/startup.sh"]
