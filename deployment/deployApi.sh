#! /bin/bash

pid=$(sudo lsof -t -i:8443)
if [[ $? == 0 ]] ; then
    sudo kill -9 ${pid}
else
    echo "Process on port 8443 was already stopped!"
fi

sudo apt-get update
sudo apt-get install git

git config --global user.name "selident"
git config --global user.email "longhoangly@gmail.com"

if [ ! -d theflashapi ]; then
  git clone git@github.com:selident/theflashapi.git 
fi

cd theflashapi
git fetch
git checkout release --force
git pull
mvn -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2 clean package
sudo java -jar target/ankiflash-0.0.1.war &