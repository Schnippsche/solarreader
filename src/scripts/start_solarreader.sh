#!/bin/sh
 JAVA=java
 JAVA_OPTS="-Dtinylog.writer.level=info -Djava.io.tmpdir=./tmp -Dport=8080 -Dsolarreader.maxthreads=5"
if [ -e "./solarreader.pid" ]; then
  echo "Solarreader appears to be already running - solarreader.pid file exists. If Solarreader is not running, please delete the file manually."
else
  # create tmp dir
  mkdir -p tmp
  # starts the jar, if you use an explicit version like solarreader-1.2.jar you must change the name here
  nohup $JAVA $JAVA_OPTS -jar solarreader.jar >/dev/null 2>&1 &
  echo $! > ./solarreader.pid
fi
