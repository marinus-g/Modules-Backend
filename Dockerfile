FROM amazoncorretto:21-alpine-jdk
LABEL authors="mischok academy"
RUN echo "Building app image"
RUN echo "Copying jar file"
COPY target/Modules-Backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]