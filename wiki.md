`solarreader` is an open source program for reading and storing inverters, battery monitors or smart meters. For easy configuration, a multilingual graphical user interface is used, which is accessed via a browser.

Since `solarreader` is based on Java, it is platform independent and runs on the operating systems Windows, Linux or Mac OS.

Ideally, you install the program on a system that runs 24/7, for example a Raspberry Pi or a Linux-based NAS like the Synology Diskstation.

Communication with the inverters is done either via TCP or RS485 sockets. Serial adapters from RS485 to USB must have an FTDI chipset.

HF2211 Wifi adapters are also supported.

Currently, external data sources can also be read out, such as openweather (weather data), awattar (electricity price exchange) or solarprognose (yield prognosis).

During configuration, an activity window can be created for each unit to be read out, where it is specified exactly from when to when and in which interval this unit is active (e.g. from 05:00 to 22:30 every 5 seconds). Furthermore, the forwarding of the read-out data can be influenced and forwarded to various databases as well as MQTT clients.

The individual device specifications are stored in json files; you can easily adapt them to your needs. To do this, copy the appropriate json file of the device directly into the directory of the jar file. This can then be edited and saved manually.

`solarreader` tests at startup if there are user specific files and uses them instead of the supplied ones.

## The device specifications contains three sections:

### Devicefields
fields based on the manufacturer specification. These are among others name, register address, length, type, unit. You can also enter a factor if you want to convert the unit directly during the readout (kilowatt to watt).

### Databasefields
are used to define the output in databases. You can specify the table name, column name, type (text or numeric) and the origin of the field.

In addition to device fields, you can also specify constants, formulas, and standard fields for the origin

### mqttfields
Used to define the MQTT export. You can specify the destination name, type and origin.

For the origin you can specify constants, formulas and standard fields as well as device fields.

**Example of a devicefield after converting from the manufacturer's protocol:**

|**Devicefield Parameter**|**Value**|**Description**|
| :- | :- | :- |
|name|PV\_Leistung1|Name for later assignment|
|unit|W|Unit is W (Watt)|
|offset|1|Modbus Register offset|
|count|2|Modbus Register count|
|register|4|Modbus Register funkcion 3 or 4<br>3 = Holding Register, 4 = Input Register|
|type|U32\_BIG\_ENDIAN|Unsigned Int in BigEndian Order |
|note|Input Power|Note|
|Factor|0.1|correction factor; final value is value \* 0.1|

This field can thus be read out and assigned on the basis of the unique name:

|**Databasefields Parameter**|**Value**|**Description**|
| :- | :- | :- |
|tablename|PV|Tablename|
|columnname|String1\_Leistung|Column Name|
|columntype|NUMBER|as number, on STRING the value is enclosed in quotation marks|
|sourcetype|RESULTFIELD|use a read out devicefield (=resultfield) as value |
|sourcevalue|PV\_Leistung1|Name of devicefield|

If the value should also be transferred to mqtt, this can be done in the mqttfields section:

|**Mqttfields Parameter**|**Value**|**Description**|
| :- | :- | :- |
|name|String1/Leistung|Exportname|
|sourcetype|RESULTFIELD|use a read out devicefield (=resultfield) as value|
|sourcevalue|PV\_Leistung1|Name of devicefield|

**Eaxmple for Calculation:**

|**Parameter**|**Value**|**Description**|
| :- | :- | :- |
|sourcetype|CALCULATED|hint for calculation|
|sourcevalue|(PV\_Leistung1 + PV\_Leistung2) / 100|The names must be present|

**Example for Constant:**

|**Parameter**|**Value**|**Description**|
| :- | :- | :- |
|sourcetype|CONSTANT|use a constant|
|sourcevalue|Version 1.0|any constant value|

**Example for standard field value:**

|**Parameter**|**Value**|**Description**|
| :- | :- | :- |
|sourcetype|STANDARDFIELD|use a standard field|
|sourcevalue|DATETIME|current date|
||||

**Example for a standard field with format:**

|**Parameter**|**Value**|**Description**|
| :- | :- | :- |
|sourcetype|STANDARDFIELD|use a standard field|
|sourcevalue|DATETIME{dd.MM.yyyy HH:mm:ss}|current date formatted|

The following standard fields are currently supported:

|**Standardfield**|**Description**|
| :- | :- |
|SECONDS|Sekunden|
|MILLISECONDS|Millisekunden|
|NANOSECONDS|Nanosekunden|
|MICROSECONDS|Microsekunden|
|DATETIME|Datum und Uhrzeit|
|DATE|Datum|
|TIME|Uhrzeit|
|HOUR|Stunde|
|WEEK|Woche|
|MONTH|Monat|
|YEAR|Jahr|
|DAYINMONTH|Tag im Monat|
|DAYINYEAR|Tag im Jahr|
|WEEKDAY|Wochentag|
|CURRENTTIMESTAMP|Aktueller Zeitstempel in Millisekunden|
|TODAYTIMESTAMP|Zeitstempel von Heute um Mitternacht in Millisekunden|
|TODAYLASTYEARTIMESTAMP|Zeitstempel von Heute vor einem Jahr in Millisekunden|
|YESTERDAYTIMESTAMP|Zeitstempel von Gestern um Mitternacht in Millisekunden|
|THISMONTHTIMESTAMP|Zeitstempel vom ersten des Monats um Mitternacht in Millisekunden|
|THISYEARTIMESTAMP|Zeitstempel des Jahres um Mitternacht in Millisekunden|
|THISWEEKTIMESTAMP|Zeitstempel der Woche um Mitternacht in Millisekunden|
|LASTMONTHTIMESTAMP|Zeitstempel letzter Monat um Mitternacht in Millisekunden|
|LASTYEARTIMESTAMP|Zeitstempel letztes Jahr um Mitternacht in Millisekunden|
|LASTWEEKTIMESTAMP|Zeitstempel letzte Woche um Mitternacht in Millisekunden|
|SUNRISE|Sonnenaufgang|
|SUNSET|Sonnenuntergang|


The following devicefield types are currently supported:

|**DeviceFieldType**|**Description**|
| :- | :- |
|STRING|Text in Big Endian|
|STRING\_LITTLE\_ENDIAN|Text in Little Endian|
|NUMBER|Numerischer value|
|BINARY|byte array|
|U8|unsigned 8 bit value|
|I8|signed 8 bit value|
|U16\_BIG\_ENDIAN|unsigned 16 bit value in Big Endian|
|I16\_BIG\_ENDIAN|signed 16 bit value in Big Endian|
|U16\_LITTLE\_ENDIAN|unsigned 16 bit value in Little Endian|
|I16\_LITTLE\_ENDIAN|signed 16 bit value in Little Endian|
|U32\_BIG\_ENDIAN|unsigned 16 bit value in Big Endian|
|I32\_BIG\_ENDIAN|signed 32 bit value in Big Endian|
|U32\_MIXED\_ENDIAN|unsigned 32 bit value in mixed Endian|
|I32\_MIXED\_ENDIAN|signed 32 bit value|
|U32\_LITTLE\_ENDIAN|unsigned 32 bit value in Little Endian|
|I32\_LITTLE\_ENDIAN|signed 32 bit value in Little Endian|
|FLOAT\_BIG\_ENDIAN|Decimal in Big Endian|
|FLOAT\_LITTLE\_ENDIAN|Decimal in Little Endian|
|DOUBLE\_BIG\_ENDIAN|Decimal double precision in Big Endian|
|DOUBLE\_LITTLE\_ENDIAN|Decimal double precision in Litte Endian|
|SCALEFACTOR\_BIG\_ENDIAN|Scaling value in I16 Big Endian , 10 ^ value|

