# Stage 1: build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN ./mvnw -v || true
RUN mvn -q -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/target/traffic-lights-*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]