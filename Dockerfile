FROM adoptopenjdk/openjdk11
COPY target/rest-blog-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]