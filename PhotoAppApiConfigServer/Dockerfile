FROM openjdk:17-jdk-alpine
VOLUME /temp
COPY target/PhotoAppApiConfigServer-0.0.1-SNAPSHOT.jar configServer.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "configServer.jar"]
