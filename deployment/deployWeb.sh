#! /bin/bash

pid=$(sudo lsof -t -i:80)
if [[ $? == 0 ]] ; then
    sudo kill -9 "${pid}"
else
    echo "Process on port 80 was already stopped!"
fi

pid=$(sudo lsof -t -i:443)
if [[ $? == 0 ]] ; then
    sudo kill -9 "${pid}"
else
    echo "Process on port 443 was already stopped!"
fi

sudo apt-get update
sudo apt-get install git
sudo apt-get install nodejs
sudo apt-get install npm

git config --global user.name "selident"
git config --global user.email "longhoangly@gmail.com"

if [ ! -d theflashweb ]; then
  git clone git@github.com:selident/theflashweb.git 
fi

cd theflashweb
git fetch
git checkout release --force
git pull

npm install --unsafe-perm
sudo npm start &