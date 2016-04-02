#!/bin/sh
mvn -q test -B

pip install --user -r PythonAnalyserLib/dependency.txt
py.test PythonAnalyserLib/test/*
