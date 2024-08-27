FROM openjdk:11 AS stage1
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY settings.gradle .
# Grant execution permissions for the gradlew file
RUN chmod +x gradlew
RUN ./gradlew bootJar
FROM openjdk:11
WORKDIR /app
COPY --from=stage1 /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]