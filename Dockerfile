FROM openjdk:8


ADD target/bot-service-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -jar bot-service-0.0.1-SNAPSHOT.jar
