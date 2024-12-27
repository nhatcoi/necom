
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





---------------------------------------------------
# Build the image
docker build -t jackie-shop:0.0.1 .

# Run the container
docker run -p 8085:8085 jackie-shop:0.0.1

docker run --name jackie-shop -p 8085:8085 -e DATABASE_URL=jdbc:postgresql://172.17.0.1:5432/jackie_shop jackie-shop:0.0.1