# Multi-stage: build with Maven+JDK, ship JRE-only (small image, small attack surface)

FROM maven:3.9-eclipse-temurin-21 AS build

ENV HTTP_PROXY=http://host.docker.internal:7890
ENV HTTPS_PROXY=http://host.docker.internal:7890
ENV http_proxy=http://host.docker.internal:7890
ENV https_proxy=http://host.docker.internal:7890

WORKDIR /app

COPY pom.xml .

COPY .mvn/settings.xml /root/.m2/settings.xml

COPY src ./src

RUN mvn -B package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/fx-app-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]