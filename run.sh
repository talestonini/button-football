#!/bin/sh
java -cp /usr/share/h2/h2*.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -baseDir /app/h2db/buttonfootball &
java -Dktor.profile=prod -jar /app/build/libs/button-football-all.jar