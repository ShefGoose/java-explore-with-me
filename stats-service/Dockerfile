FROM maven:3.9.7-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .

RUN mvn -q -B -pl stats-service/stats-server -am dependency:go-offline
RUN mvn -q -B -pl stats-service/stats-server -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/stats-service/stats-server/target/stats-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java","-jar","/app/app.jar"]