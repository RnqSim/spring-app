FROM maven:3.8.6-openjdk-18 AS build
COPY . .
RUN mvn clean package -DskipTests

#OAUTH2_PROXY_UPSTREAMS: "static://200"

FROM openjdk:18-jdk-slim
COPY --from=build /target/clinics-0.0.1-SNAPSHOT.jar clinics.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","clinics.jar"]
ENTRYPOINT [ "sh", "-c", "/bin/oauth2-proxy --config=/etc/oauth2_proxy.cfg --upstream=http://${UPSTREAM}" ]
