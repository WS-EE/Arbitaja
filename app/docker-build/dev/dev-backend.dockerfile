FROM maven:latest AS build
WORKDIR /opt/arbitaja/
COPY ./pom.xml /opt/arbitaja/

CMD [ "mvn", "spring-boot:run" ]