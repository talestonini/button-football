FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY . /app
RUN chmod +x /app/run.sh

# expose database and app ports
EXPOSE 9092 8080

# download OS dependencies
RUN apt-get update && apt-get install -y wget unzip \
    && wget https://services.gradle.org/distributions/gradle-8.10-bin.zip -P /tmp \
    && unzip -d /opt/gradle /tmp/gradle-8.10-bin.zip \
    && ln -s /opt/gradle/gradle-8.10/bin/gradle /usr/bin/gradle

# build the app
RUN gradle clean build shadowJar

# unzip h2 binaries
RUN unzip -d /usr/share/h2 /app/h2bin/h2-2024-08-11.zip

# run H2 as server and the app
CMD ["/app/run.sh"]