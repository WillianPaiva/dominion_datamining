#!/bin/sh
mvn -q test -B

pip install -r PythonAnalyserLib/dependency.txt
pip install --upgrade pip
py.test PythonAnalyserLib/test/*
