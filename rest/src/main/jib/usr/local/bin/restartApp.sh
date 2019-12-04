#!/bin/bash

## CRONTAB ENTRY
## m h  dom mon dow   command
#45 05 * * * /bin/sh /usr/local/bin/restartApp.sh

LOGFILE=/var/log/run.log

echo "Restarting application on cron" >> $LOGFILE
ps -ef | grep '[j]ava' | awk '{print $2}' | xargs kill
sleep 1
cd /
nohup /opt/java/openjdk/bin/java -Xms256m -Xmx256m -cp /app/resources:/app/classes:/app/libs/* tesl.Application >> $LOGFILE 2>&1 &