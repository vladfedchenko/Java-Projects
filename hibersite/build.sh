#!/bin/bash

mvn clean compile assembly:single
read -n1 -r -p "Press any key to continue..." key
