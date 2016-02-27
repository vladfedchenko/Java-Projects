#!/bin/bash

java -cp "lib/jade.jar:out/production/JadeChat" jade.Boot -gui -agents "Adam:vladfedchenko.jade.chat.ChatAgent;Eve:vladfedchenko.jade.chat.ChatAgent"
