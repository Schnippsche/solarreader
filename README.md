# solarreader

`solarreader` was inspired by the `solaranzeige` project from Ulrich Kunz, which is written in php and has some limitations.

`solarreader` is a java daemon for collecting measurement data from smart meters and grid inverters with a multi language gui for easy
configuration.
All collected data can be exported to influx databases v1 / v2 and MQTT clients.
A buffer for Influx and mqtt requests exists; it can hold the request for 30 minutes ore more in memory.
So you didn't lost any information if influxdb is currently busy or unreachable.

Modbus communication is possible over RS485 connections as well as TCP sockets.

`solarreader` can also import data from Openweather, aWattar and Solarprognose.
The output of `solarreader` is compatible with the `solaranzeige` project.

You can use multiple devices, influx databases and mqtt destinations.

## Requirements

You'll need:

* A supported smart meter or grid inverter.
* In case of Modbus/RTU: an USB RS485 adapter with FTDI chipset.
* Optionally an RS485 to Ethernet converter
* Java 8 or higher

## Installation on Linux

`solarreader` needs java 8 or higher, and works well with openjdk.
To install openjdk, type the following in your terminal:

```bash
sudo apt install default-jdk
```

After installation, verifiy it with the following command:

```bash
java -version
```

Note for Linux users: Serial port access is limited to certain users and groups in Linux. To enable user access, you
must open a terminal and enter the following commands before solarreader will be able to access the ports on your
system.
Don't worry if some of the commands fail. All of these groups may not exist on every Linux distro. (Note, this process
must only be done once for each user):

```bash
sudo usermod -a -G uucp pi
sudo usermod -a -G dialout pi
sudo usermod -a -G lock pi
sudo usermod -a -G tty pi
```

Replace the username parameter with your current username. (If you are not sure what your username is, type whoami and
it will tell you.) If you are using SUSE 11.3 or higher, replace the '-a -G' flags with a single '-A' flag.

Note for Synology users and RS485-USB adapters with FTDI chipset:
Activate the build-in FTDI drivers with following commands:

```bash
sudo insmod /lib/modules/usbserial.ko
sudo insmod /lib/modules/ftdi_sio.ko
sudo chmod 777 /lib/modules/usbserial.ko
sudo chmod 777 /lib/modules/ftdi_sio.ko
```
You have to do that after every reboot, so it is recommended to put it in a start script.

Download the archive and unzip the files. Then run the startup.sh (Linux) or startup.bat (Windows).
To show the GUI type "locahost:8080" in your browser (or the ip of your computer)

## Supported devices

- Schueco SG inverter
- Growatt inverter
- Solaredge devices
- SDM230 smart meter
- SDM630 smart meter
- Goodwe inverter
- Phocos inverter
- many more planned and in progress... Feel free to contribute and share it with the community!

### Using the precompiled binaries

Precompiled release packages are [available](https://github.com/Schnippsche/Solarreader/releases). Download
the jar and optional the [start script](https://github.com/Schnippsche/solarreader/tree/main/src/scripts) for your platform 

### Building from source

`solarreader` is written in java and requires jdk 8+. To build from source:

- use `mvn clean package` to compile and build the jar file

## Contribute

Feel free to add new devices! You need the specification protocol from your device.

For version change history have a look at
the [ChangeLog](https://github.com/Schnippsche/Solarreader/blob/main/CHANGELOG.md).

### Build Requirements

* Java 1.8+
* Maven 3.5+

You can build solarreader.jar with all tests with:

```bash
$> mvn clean package
```
