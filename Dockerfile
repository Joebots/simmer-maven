FROM tomcat:8.0
MAINTAINER joebotics
# Must perform a mvn install first
COPY target/simmer-1.1.0-SNAPSHOT.war /usr/local/tomcat/webapps/
COPY docker/tomcat-users.xml /usr/local/tomcat/conf
