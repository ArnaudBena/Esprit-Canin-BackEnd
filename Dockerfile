FROM eclipse-temurin:21-jre-alpine
WORKDIR app
COPY target/app.jar app.jar
COPY .env .env
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]