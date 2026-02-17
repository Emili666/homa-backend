# ==============================================================
# ETAPA 1: Build - Compilar con Gradle
# ==============================================================
FROM gradle:8.7.0-jdk17 AS build

WORKDIR /app

# Copiar archivos de build
COPY build.gradle settings.gradle gradlew ./
COPY gradle/ ./gradle/

# Copiar código fuente
COPY src/ ./src/

# Build de la aplicación (sin daemon para reducir memoria)
RUN gradle clean bootJar --no-daemon -x test

# ==============================================================
# ETAPA 2: Runtime - Imagen final optimizada
# ==============================================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar curl para healthchecks
RUN apk add --no-cache curl

# Crear usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar JAR desde etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar permisos y propietario
RUN chown -R spring:spring /app
USER spring

# Health check usando Actuator endpoint
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Exponer puerto
EXPOSE 8080

# Ejecutar aplicación con opciones JVM optimizadas
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
