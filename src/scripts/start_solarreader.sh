#!/bin/sh
 JAVA=java
 JAVA_OPTS="-Dtinylog.writer.level=debug -Djava.io.tmpdir=./tmp -Dport=8080 -Dsolarreader.maxthreads=5"
if [ -e "./solarreader.pid" ]; then
  echo "Solarreader appears to be already running - solarreader.pid file exists. If Solarreader is not running, please delete the file manually."
else
  nohup $JAVA $JAVA_OPTS -jar solarreader-1.0.jar 2>&1 &
  echo $! > ./solarreader.pid
fi
