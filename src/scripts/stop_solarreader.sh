#!/bin/bash
if [ -r "./solarreader.pid" ]; then
  kill "$(cat ./solarreader.pid)"
  rm ./solarreader.pid
else
  echo "solarreader.pid file does not exist - Solarreader appears not to be running."
fi