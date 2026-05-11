# ---- Build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY services/user-service/pom.xml .
RUN mvn dependency:go-offline -q
COPY services/user-service/src ./src
RUN mvn package -DskipTests -q

# ---- Runtime ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
RUN addgroup --system appgroup \
 && adduser --system --ingroup appgroup appuser
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8081
ENTRYPOINT ["java", \
  "-XX:InitialRAMPercentage=50", \
  "-XX:MaxRAMPercentage=75",     \
  "-jar", "app.jar"]

