#!/bin/bash

git submodule update --init --recursive

cd database/

scons all

cd ../

mkdir sampleLogs/temp


mvn clean compile assembly:single

java -jar target/dominion_datamining-1.0-SNAPSHOT-jar-with-dependencies.jar
