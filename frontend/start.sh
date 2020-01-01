#!/bin/sh
set -x #echo on

EXECUTING_FOLDER="$(dirname "$0")"

echo $EXECUTING_FOLDER
cd $EXECUTING_FOLDER

npm start
http-server ./dist &

sleep 1s
sensible-browser http://localhost:8080/ &

grunt watch

