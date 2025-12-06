FROM maven:3.9-eclipse-temurin-17 AS build
LABEL authors="plgdhd"
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dskiptests -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]