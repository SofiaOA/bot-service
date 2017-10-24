FROM openjdk:8


ADD target/bot-service-0.0.1-SNAPSHOT.jar /

ENTRYPOINT java -XX:+UseSerialGC -jar bot-service-0.0.1-SNAPSHOT.jar
