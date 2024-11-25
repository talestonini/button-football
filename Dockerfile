FROM eclipse-temurin:21-jdk-jammy
RUN apt-get update && apt-get install -y h2 wget unzip \
    && wget https://services.gradle.org/distributions/gradle-8.10-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-8.10-bin.zip \
    && ln -s /opt/gradle/gradle-8.10/bin/gradle /usr/bin/gradle
WORKDIR /app
COPY . /app
RUN gradle clean build shadowJar
CMD ["/app/run.sh"]
EXPOSE 9092, 8080