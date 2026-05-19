FROM maven:3.9.9-eclipse-temurin-23
WORKDIR /opt/arbitaja/
COPY ./pom.xml /opt/arbitaja/
RUN mvn dependency:copy-dependencies

CMD [ "mvn", "spring-boot:run" ]
