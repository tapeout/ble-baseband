#!/bin/bash
set -e
echo "Building pdatop.v"
sbt 'runMain elaborate_pdatop'
mv ble_pdatop.v pdatop.v
echo "pdatop.v created."
