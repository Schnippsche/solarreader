#!/bin/sh
 JAVA=java
 # tinylog.writer.level: possible values are debug, info, warn, and error, see https://tinylog.org/v2/configuration/#severity-level
 # tinylog.writer.backups: the maximum number of log files that should be kept, see https://tinylog.org/v2/configuration/#rolling-file-writer
 # solarreader.maxthreads: optimal setting is your device count + 5; so if you have 10 devices set it to 15
 JAVA_OPTS="-Dtinylog.writer.level=info -Dtinylog.writer.backups=30 -Djava.io.tmpdir=./tmp -Dport=8080 -Dsolarreader.maxthreads=15"
if [ -e "./solarreader.pid" ]; then
  echo "Solarreader appears to be already running - solarreader.pid file exists. If Solarreader is not running, please delete the file manually."
else
  # create tmp dir
  mkdir -p tmp
  # starts the jar, if you use an explicit version like solarreader-1.2.jar you must change the name here
  nohup $JAVA $JAVA_OPTS -jar solarreader.jar >/dev/null 2>&1 &
  echo $! > ./solarreader.pid
fi
