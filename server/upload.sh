#!/bin/bash
mvn clean install && scp -r target  handcontrol@paulrozhkin.ru:/home/handcontrol/server

