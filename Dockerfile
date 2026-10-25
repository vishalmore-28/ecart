FROM tomcat:9.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml
ADD https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar /usr/local/tomcat/lib/
COPY ecart.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
