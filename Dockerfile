# Builder stage, compile the source into a war file
FROM  maven:3.8.6-jdk-8 AS builder
ADD ./pom.xml pom.xml
ADD ./src src/
ADD ./package.json package.json
ADD ./webpack.config.js webpack.js
RUN mvn clean package

# Run stage, copy the war file into the web container
FROM tomcat:8.0
LABEL maintainer joebotics
COPY --from=builder target/simmer-1.1.0-SNAPSHOT.war /usr/local/tomcat/webapps/simmer.war
COPY docker/tomcat-users.xml /usr/local/tomcat/conf