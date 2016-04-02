#!/bin/sh
mvn -q test -B

pip install --upgrade pip
pip install -r PythonAnalyserLib/dependency.txt
py.test PythonAnalyserLib/test/*
