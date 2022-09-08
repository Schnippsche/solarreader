@echo off
set JAVA=java
set JAVA_OPTS="-Dtinylog.writer.level=debug -Djava.io.tmpdir=./tmp -Dport=8080 -Dsolarreader.maxthreads=5"
$JAVA $JAVA_OPTS -jar solarreader-1.0.jar





