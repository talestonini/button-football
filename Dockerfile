FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY ./build/libs/button-football-all.jar /app
COPY ./h2db/buttonfootball.mv.db /app/h2db/
CMD ["java", "-Dktor.profile=prod", "-jar", "/app/button-football-all.jar"]
EXPOSE 8080