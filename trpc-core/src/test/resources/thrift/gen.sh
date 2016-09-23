#!/bin/bash
SCRIPT_DIR=`dirname $0`
thrift --gen java -out ${SCRIPT_DIR}/../../java hello.thrift