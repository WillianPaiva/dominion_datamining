#!/bin/sh
pip install --user -r PythonAnalyserLib/dependency.txt
py.test PythonAnalyserLib/test/*
