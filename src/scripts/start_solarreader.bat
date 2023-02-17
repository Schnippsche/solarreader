@echo off
set JAVA=java
set JAVA_OPTS="-Dtinylog.writer.level=info -Dtinylog.writer.backups=8 -Dtinylog.writer.latest=latest.log -Djava.io.tmpdir=./tmp -Dport=8080 -Dsolarreader.maxthreads=15"
$JAVA $JAVA_OPTS -jar solarreader.jar





