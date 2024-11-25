#!/bin/sh
java -cp ./h2/lib/*.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -baseDir ./h2/db &
java -Dktor.profile=dev -jar ./build/libs/button-football-all.jar