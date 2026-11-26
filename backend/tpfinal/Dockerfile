# ---------- Etapa 1: compilar el proyecto ----------
# Esta imagen ya trae Maven y el JDK 17, así que no hace falta instalar nada.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Genera el .jar ejecutable (los tests se omiten: el de contexto necesita la base de datos)
RUN mvn -B -q -DskipTests package

# ---------- Etapa 2: imagen final, liviana ----------
# Solo el JRE (para ejecutar) y el .jar generado en la etapa anterior.
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Límites de memoria pensados para el plan gratuito de Render (512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

# Render define la variable PORT; application.properties la lee (server.port=${PORT:8080})
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
