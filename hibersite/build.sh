#!/bin/bash

mvn clean compile package
read -n1 -r -p "Press any key to continue..." key
