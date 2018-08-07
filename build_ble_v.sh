#!/bin/bash
set -e
echo "Building ble.v"
sbt 'runMain elaborate_ble'
echo "ble.v created. You can remove or ignore the 'ble' module in ble.v as it is unused"
