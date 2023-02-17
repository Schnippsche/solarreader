package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import org.tinylog.Logger;

import java.text.DecimalFormat;

public class Schueco extends AbstractLockedDevice
{
  private String typeCommand;
  private String sendCommand;
  private String usbDevice;
  private String expectedResponseCode;
  private SimpleSerialConnection simpleSerialConnection;

  public Schueco(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification("schueco");
    usbDevice = getConfigDevice().getParam(ConfigDeviceField.COM_PORT);
    int deviceAddress = getConfigDevice().getIntParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, 1);
    String device = new DecimalFormat("00").format(deviceAddress);
    this.typeCommand = "#" + device + "9\r";
    this.sendCommand = "#" + device + "0\r";
    this.expectedResponseCode = "*" + device + "0";
    this.simpleSerialConnection = new SimpleSerialConnection(getConfigDevice());
  }

  @Override public boolean checkConnection()
  {
    initialize();
    return simpleSerialConnection.checkConnection();
  }

  @Override protected boolean readLockedDeviceValues()
  {
    String receive = null;
    String type = "";
    try
    {
      if (!simpleSerialConnection.open())
      {
        return false;
      }
      // Got cached value ?
      ResultField modelField = getConfigDevice().getCachedResultField("modell");
      if (modelField == null)
      {
        simpleSerialConnection.send(this.typeCommand);
        type = simpleSerialConnection.readCrStringLf();
        if (type == null || type.length() < 5)
        {
          Logger.error("Not enough data");
        } else
        {
          type = type.substring(5);
          modelField = new ResultField("modell", ResultFieldStatus.VALID, FieldType.STRING, type);
          resultFields.add(modelField);
          getConfigDevice().getCachedResultFields().add(modelField);
        }
      } else
      {
        Logger.debug("get cached result field");
        resultFields.add(modelField);
      }
      // main data
      simpleSerialConnection.send(this.sendCommand);
      receive = simpleSerialConnection.readCrStringLf();
      // Die zurückgelieferten Daten müssen 56 Zeichen haben! Könnte sich einmal ändern!
      // A  Antwortzeichen        4 Stellen
      // S  Status                3 Stellen
      // U  Generatorspannung     5 Stellen
      // I  Generatorstrom        5 Stellen
      // P  Generatorleistung     5 Stellen
      // UN Netzspannung          5 Stellen
      // IN Netz- / Einspeisestrom 5 Stellen
      // PN Eingespeiste Leistung  5 Stellen
      // T  Gerätetemperatur      3 Stellen
      // E  Tagesenergie          6 Stellen
      // F  Prüfsumme             1 Stelle
      // ...A ..S ....U ....I ....P ...UN ...IN ...PN ..T .....E F
      // *010   4 350.0  1.18   414 229.2  1.74   398  31   1139 x
      // ST1 A S U I P UN IN PN T E F WR ST2
      // *010   4 357.5  0.40   144 230.9  0.60   138  29     59 a
      if (receive == null || receive.length() < 57)
      {
        Logger.error("data is empty or too short!");
        return false;
      }
      Logger.debug("Receive:{}", receive);
      if (!receive.startsWith(this.expectedResponseCode))
      {
        String resp = receive.substring(0, 4);
        Logger.error("unexpected response code '{}', expected '{}'", resp, this.expectedResponseCode);
        return false;
      }
      // everything is ok
      for (DeviceField deviceField : this.specification.getDevicefields())
      {
        String value =
          receive.substring(deviceField.getOffset(), deviceField.getOffset() + deviceField.getCount()).trim();
        resultFields.add(new ResultField(deviceField, ResultFieldStatus.VALID, value));
      }

      return true;

    } catch (Exception e)
    {
      Logger.error("error while reading {}: {}", usbDevice, e);
      return false;
    } finally
    {
      simpleSerialConnection.close();
    }
  }

}




