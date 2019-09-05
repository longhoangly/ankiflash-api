#! /bin/bash

cd theflashapi
sudo mvn clean package
sudo java -jar target/ankiflash-*.jar &