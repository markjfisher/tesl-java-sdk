#!/bin/bash

## CRONTAB ENTRY
## m h  dom mon dow   command
#45 05 * * * /bin/sh /usr/local/bin/restartApp.sh

LOGFILE=/var/log/run.log
export TESL_BOT_TOKEN=FIX_ME
#export TESL_BOT_CMD_POST_FIX=-test
#export TESL_BOT_SCAN_MESSAGES=false

echo "Restarting application on cron" >> $LOGFILE
ps -ef | grep '[j]ava' | awk '{print $2}' | xargs kill
sleep 1
cd /
nohup /opt/java/openjdk/bin/java -Xms256m -Xmx256m -cp /app/resources:/app/classes:/app/libs/* tesl.bot.BotCheck >> $LOGFILE 2>&1 &