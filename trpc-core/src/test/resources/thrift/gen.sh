#!/bin/bash
rm -rf src
mkdir -p src/main/java
thrift --gen java -out ../../java hello.thrift
if [ $? != 0 ]; then
	exit 1
fi
