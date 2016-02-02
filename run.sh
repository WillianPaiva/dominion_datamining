#!/bin/bash
mkdir -p mongodb >/dev/null 2>&1
mkdir -p mongoData
mkdir -p mongoLog

if [ ! -f mongodb/mongodb-linux-x86_64-3.2.1/bin/mongod ]; then
    echo "downloading mongodb"
    curl -O https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-3.2.1.tgz
    tar -zxf mongodb-linux-x86_64-3.2.1.tgz -C mongodb
fi

mongodb/mongodb-linux-x86_64-3.2.1/bin/mongo --eval "db.stats()" > /dev/null
RESULT=$?   # returns 0 if mongo eval succeeds
if [ $RESULT -ne 0 ]; then
    echo "starting database engine"
    mongodb/mongodb-linux-x86_64-3.2.1/bin/mongod --dbpath mongoData --quiet --fork --logpath mongoLog/mongod.log
    mkdir sampleLogs/temp >/dev/null 2>&1
else
    echo "mongodb already running!"
fi

echo "Compiling Dominion data mining"
mvn -q clean compile assembly:single

java -jar target/dominion_datamining-1.0-SNAPSHOT-jar-with-dependencies.jar
