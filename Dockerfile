FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml settings.xml ./
RUN mvn -B -s settings.xml dependency:go-offline

COPY src ./src
# 加上 -s settings.xml
RUN mvn -B -s settings.xml package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/fx-app-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]