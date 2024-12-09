FROM maven:latest AS build
WORKDIR /app
COPY ./pom.xml /app
COPY ./src /app/src
RUN mvn clean install -U

FROM openjdk:23
WORKDIR /opt/arbitaja/
COPY --from=build /app/target/*.jar /opt/arbitaja/backend.jar
CMD ["java", "-jar", "backend.jar"]
