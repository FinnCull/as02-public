FROM openjdk:19-jdk-alpine

WORKDIR /app

COPY ./target/musicfinder-0.0.1-SNAPSHOT.jar.original /app/musicfinder.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/musicfinder.jar"]
