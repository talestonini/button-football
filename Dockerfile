FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY . /app
RUN chmod +x /app/run.sh
EXPOSE 8080
RUN apt-get update && apt-get install -y wget unzip \
    && wget https://services.gradle.org/distributions/gradle-8.10-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-8.10-bin.zip \
    && ln -s /opt/gradle/gradle-8.10/bin/gradle /usr/bin/gradle
RUN gradle clean build shadowJar
CMD ["/app/run.sh"]