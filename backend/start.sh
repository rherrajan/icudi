#!/bin/sh
set -x #echo on

mvn clean install
heroku local

