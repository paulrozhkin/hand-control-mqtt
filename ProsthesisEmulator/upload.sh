#!/bin/bash
# firstly delete emulator/target dir on the server!
mvn clean install && scp -r target  handcontrol@paulrozhkin.ru:/home/handcontrol/emulator

# run on server:
# cd emulator
# java -Dlog4j.configurationFile=log4j2-emulator.xml -jar target/consoleApp-1.0-jar-with-dependencies.jar &
# logs in: emulator/logs/ (set up in log4j2-emulator.xml) 
