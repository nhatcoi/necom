
# stage 1
FROM maven:3.9.9-amazoncorretto-21-debian AS build

WORKDIR /app
COPY . /app

RUN mvn package -DskipTests

# stage 2
FROM amazoncorretto:21.0.5

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]