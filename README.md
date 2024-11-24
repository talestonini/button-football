# Button Football

## Fat JAR

    ./gradlew clean shadowJar

## Dockerize

    docker build -t button-football .

## Run Container

    docker run -p 8080:8080 button-football

## Database Backup

    java -cp h2*.jar org.h2.tools.Backup -file "/Users/talestonini/dev/repos/button-football/h2db/h2-button-football-backup.zip" -dir "/Users/talestonini/dev/repos/button-football/h2db" -db "buttonfootball"