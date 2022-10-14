#!/bin/sh
# script for starting the application after reboot
# Place it in the /etc/init.d directory or
# add following line to your crontab:
# @reboot sleep 20 &&  /etc/init.d/reboot_solarreader.sh
# or add this to the /etc/rc.local file:
# /etc/init.d/reboot_solarreader.sh
#
# make this file executable with chmod 755 reboot_solarreader.sh
#
# path to the directory where the solarreader.jar is
SOLARREADER_PATH='/home/pi/Solarreader'
# delete the pid file
sudo rm -f $SOLARREADER_PATH/solarreader.pid
# start solarreader script
cd $SOLARREADER_PATH/ && ./start_solarreader.sh