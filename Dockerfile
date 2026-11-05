# ---------- stage 1: build the WAR from source (works on Render, Docker, AWS) ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests

# ---------- stage 2: run on Tomcat 9 ----------
FROM tomcat:9.0-jdk17-temurin
RUN rm -rf /usr/local/tomcat/webapps/* \
 && sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml
COPY --from=build /app/target/ecart.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
# Database + admin are configured with environment variables (see README):
#   DB_URL, DB_USER, DB_PASSWORD, ADMIN_EMAIL, ADMIN_PASSWORD   (DB_DRIVER is optional)
