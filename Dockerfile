# Mutil-stage to build the lighest and optimized image

# stage 1
FROM maven:3.9.9-amazoncorretto-21-debian AS build

WORKDIR /app
COPY ecommerce-web-key.json .
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests && rm -f /app/ecommerce-web-key.json

# stage 2
FROM amazoncorretto:21.0.6-al2023-headless

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
