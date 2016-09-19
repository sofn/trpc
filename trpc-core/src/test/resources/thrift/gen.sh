#!/bin/bash
SCRIPT_DIR=`dirname $0`
thrift --gen java -out ${SCRIPT_DIR}/../../java fb303.thrift
thrift --gen java -out ${SCRIPT_DIR}/../../java hello.thrift