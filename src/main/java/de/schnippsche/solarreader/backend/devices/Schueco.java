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
      simpleSerialConnection.send(this.typeCommand);
      // Dont use wait() because we want to hold locking
      Thread.sleep(500);
      type = simpleSerialConnection.readCrStringLf(13);
      // something received ?
      if (type.length() > 2)
      {
        simpleSerialConnection.send(this.sendCommand);
        // Dont use wait() because we want to hold locking
        Thread.sleep(1000);
        receive = simpleSerialConnection.readCrStringLf(57);
      } else
      {
        Logger.error("Not enough data");
        return false;
      }
    } catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    } catch (Exception e)
    {
      Logger.error("error while reading {}: {}", usbDevice, e);
      return false;
    } finally
    {
      simpleSerialConnection.close();
    }

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
    if (receive == null || receive.isEmpty())
    {
      Logger.error("data is empty!");
      return false;
    }
    Logger.debug("Receive:{}", receive);
    if (receive.length() < 57)
    {
      Logger.error("not enough data, only {} bytes read", receive.length());
      return false;
    }
    if (!receive.startsWith(this.expectedResponseCode))
    {
      String resp = receive.substring(0, 4);
      Logger.error("unexpected response code '{}', expected '{}'", resp, this.expectedResponseCode);
      return false;
    }

    if (type.length() > 5)
    {
      type = type.substring(5);
    }

    resultFields.add(new ResultField("modell", ResultFieldStatus.VALID, FieldType.STRING, type));
    for (DeviceField deviceField : this.specification.getDevicefields())
    {
      String value =
        receive.substring(deviceField.getOffset(), deviceField.getOffset() + deviceField.getCount()).trim();
      resultFields.add(new ResultField(deviceField, ResultFieldStatus.VALID, value));
    }

    return true;
  }

}
