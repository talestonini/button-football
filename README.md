# Button Football

## Dev Run

    ./gradlew clean run

## Fat JAR

    ./gradlew clean shadowJar

## Dockerize

    docker build -t button-football .

## Run Container

    docker run -p 8080:8080 button-football

## Database Backup

    java -cp h2*.jar org.h2.tools.Backup -file "./h2dbbkp/h2-button-football-backup.zip" -dir "./h2db" -db "buttonfootball"