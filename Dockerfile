FROM maven:3.8.3-openjdk-17 AS build

COPY pom.xml .

RUN mvn -B dependency:go-offline

COPY src src
COPY checkstyle.xml .
COPY lombok.config .

RUN mvn -B package -DskipTests

FROM openjdk:17
COPY --from=build target/asku-magazine-service.jar .
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "asku-magazine-service.jar"]