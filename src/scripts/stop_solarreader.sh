#!/bin/bash
if [ -r "./solarreader.pid" ]; then
  pid="$(cat ./solarreader.pid)"
  kill $pid
  # Warten, bis der Prozess beendet wird
  sleep 5
  # Prüfen, ob der Prozess immer noch läuft
  if ps -p $pid > /dev/null
  then
    # Prozess erzwingen beenden
	kill -9 $pid
  fi
  rm ./solarreader.pid
else
  echo "solarreader.pid file does not exist - Solarreader appears not to be running."
fi