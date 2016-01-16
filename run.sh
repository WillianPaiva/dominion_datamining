#!/bin/bash


mvn clean compile assembly:single

java -jar target/dominion_datamining-1.0-SNAPSHOT-jar-with-dependencies.jar
