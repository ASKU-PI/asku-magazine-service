FROM openjdk:16

ADD ./target/asku-magazine-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/asku-magazine-service.jar"]

EXPOSE 8891