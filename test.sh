#!/bin/sh
mvn -q test -B

cd PythonAnalyserLib
pip install -r dependency.txt
py.test test/*
