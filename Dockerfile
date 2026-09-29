# Stage 1: Build with Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2: Production runtime image with standard Debian-based Temurin 21 JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create temp directory with full permissions for Tomcat embedded engine
RUN mkdir -p /tmp && chmod 777 /tmp

COPY --from=build /app/target/*.jar app.jar

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=h2
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Djava.io.tmpdir=/tmp", "-jar", "app.jar"]
