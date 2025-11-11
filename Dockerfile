# Etapa 1: construir el proyecto con Gradle y JDK 17
FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# Etapa 2: imagen final solo con el JDK 17
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
