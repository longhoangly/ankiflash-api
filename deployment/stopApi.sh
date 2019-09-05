#! /bin/bash

pid=$(sudo lsof -t -i:8443)
if [[ $? == 0 ]] ; then
    sudo kill -9 $pid
    echo "Stopped theflash API"!
else
    echo "Process on port 8443 was already stopped!"
fi