FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/traffic-lights-1.0.0.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
