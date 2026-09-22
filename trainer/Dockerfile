FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml ./
COPY shared-libs/pom.xml shared-libs/
COPY trainer/pom.xml trainer/
COPY fitness/pom.xml fitness/
COPY EurekuServer/pom.xml EurekuServer/
COPY integration-tests/pom.xml integration-tests/

COPY shared-libs/src/main shared-libs/src/main
COPY trainer/src/main trainer/src/main

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl trainer -am clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/trainer/target/*.jar app.jar
USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
