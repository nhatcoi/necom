FROM eclipse-temurin:17-jre-alpine

WORKDIR /necom-server

# Sao chép tệp JAR đã build vào container
COPY target/necom-0.0.1-SNAPSHOT.jar /necom-server/necom-0.0.1-SNAPSHOT.jar

EXPOSE 8085

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Chạy ứng dụng Spring Boot với JAVA_OPTS (tối ưu cho VPS RAM 2GB: -Xmx384m -Xms192m)
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:--Xmx384m -Xms192m} -jar necom-0.0.1-SNAPSHOT.jar"]
