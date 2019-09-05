#! /bin/bash

pid=$(sudo lsof -t -i:80)
if [[ $? == 0 ]] ; then
    sudo kill -9 $pid
    echo "Stopped theflash HTTP"
else
    echo "Process on port 80 was already stopped!"
fi

pid=$(sudo lsof -t -i:443)
if [[ $? == 0 ]] ; then
    sudo kill -9 $pid
    echo "Stopped theflash HTTPS"
else
    echo "Process on port 443 was already stopped!"
fi