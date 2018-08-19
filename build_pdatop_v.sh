#!/bin/bash
set -e
echo "Building pdatop.v"
sbt 'runMain elaborate_pdatop'
echo "pdatop.v created."
