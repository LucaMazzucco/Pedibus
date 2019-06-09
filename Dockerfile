FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY ./src/main/data /src/main/data
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=mongodb://mongo/pedibus","-jar","app.jar"]