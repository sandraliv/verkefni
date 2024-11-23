FROM maven:3-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -Dskiptests

FROM eclipse-temurin:17-alpine
COPY --from=build /target/verkefni-0.0.1-SNAPSHOT.jar.original verkefni.jar
EXPOSE 8080
ENTRYPOINT ["java", "jar", "demo.jar"]
