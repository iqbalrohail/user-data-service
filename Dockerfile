FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-11-jdk -y
COPY . .
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/user-0.0.1-SNAPSHOT.jar"]