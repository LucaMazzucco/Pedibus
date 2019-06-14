FROM openjdk:8-jdk-alpine
EXPOSE 8080
COPY ./src/main/data /src/main/data
COPY ./target/pedibus-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-Dspring.data.mongodb.uri=mongodb://mongo/pedibus","-jar","app.jar"]
