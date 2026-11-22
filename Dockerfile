# Etapa de construcción
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/discord-music-bot-1.0-SNAPSHOT.jar .

# Variables de entorno y comando de inicio
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "discord-music-bot-1.0-SNAPSHOT.jar"]