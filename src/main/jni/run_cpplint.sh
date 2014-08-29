#!/bin/sh
PYTHON_BIN=C:\\Python26\\python.exe

if [ ! -f ${PYTHON_BIN} ]; then
	PYTHON_BIN=python
fi

${PYTHON_BIN} cpplint.py wrapper/*.cpp wrapper/*.h nativesocket/*.cpp nativesocket/*.h
read