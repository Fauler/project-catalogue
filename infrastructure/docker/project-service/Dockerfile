# ---- Build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY services/project-service/pom.xml .
RUN mvn dependency:go-offline -q
COPY services/project-service/src ./src
RUN mvn package -DskipTests -q

# ---- Runtime ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
RUN addgroup --system appgroup \
 && adduser --system --ingroup appgroup appuser
COPY --from=build /app/target/project-service-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar
USER appuser
EXPOSE 8082
# Set JAVA_DEBUG_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 to enable remote debug
ENV JAVA_DEBUG_OPTS=""
ENTRYPOINT ["sh", "-c", "java \
  -XX:InitialRAMPercentage=50 \
  -XX:MaxRAMPercentage=75 \
  ${JAVA_DEBUG_OPTS} \
  -jar app.jar"]

