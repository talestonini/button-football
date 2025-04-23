# Button Football

## Dev Run

    source set_env.sh
    ./gradlew clean run

## Fat JAR

    ./gradlew clean shadowJar

## Dockerize

    docker build -t button-football .

## Run Container

    docker run -p 8080:8080 button-football

## Run Database Locally

    java -cp ./h2/lib/h2-2.3.232.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -baseDir ./h2/db

## Database Backup

    java -cp h2*.jar org.h2.tools.Backup -file "./h2dbbkp/h2-button-football-backup.zip" -dir "./h2db" -db "buttonfootball"

**NOTE**: The command above is actually not in use.  Backups are taken from the server in GCP and stored in the database
repo.

## TODOs/FIXMEs

N/A