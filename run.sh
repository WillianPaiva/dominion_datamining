#!/bin/bash
mkdir -p mongodb >/dev/null 2>&1
mkdir -p mongoData
mkdir -p mongoLog
mkdir -p sampleLogs/temp
mkdir -p h2data
RED='\033[33;31m'
NC='\033[0m'

if [ ! -f mongodb/mongodb-linux-x86_64-3.2.1/bin/mongod ]; then
    echo -e "${RED}downloading mongodb${NC}"
    curl -O https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-3.2.1.tgz
    tar -zxf mongodb-linux-x86_64-3.2.1.tgz -C mongodb
fi

mongodb/mongodb-linux-x86_64-3.2.1/bin/mongo --eval "db.stats()" > /dev/null
RESULT=$?   # returns 0 if mongo eval succeeds
if [ $RESULT -ne 0 ]; then
    echo -e "${RED}starting database engine${NC}"
    mongodb/mongodb-linux-x86_64-3.2.1/bin/mongod --dbpath mongoData --quiet --fork --logpath mongoLog/mongod.log
else
    echo -e "${RED}mongodb already running!${NC}"
fi

echo -e "${RED}Compiling Dominion data mining${NC}"
mvn -q clean compile assembly:single

echo -e "${RED}Running Dominion datamining{NC}"
java -jar target/dominion_datamining-1.0-SNAPSHOT-jar-with-dependencies.jar
