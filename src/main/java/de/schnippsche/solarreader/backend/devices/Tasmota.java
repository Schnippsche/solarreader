package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.sonoff.SensorValue;
import de.schnippsche.solarreader.backend.serializes.sonoff.SonOffWrapper;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tasmota extends AbstractDevice
{
  private String url;
  private SonOffWrapper sonOffWrapper;

  public Tasmota(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    url =
      String.format("http://%s:%s/cm?cmnd=Status0", getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost"), getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80"));
  }

  @Override protected boolean readDeviceValues()
  {
    Logger.info("try to connect to " + url);
    sonOffWrapper = jsonTool.getObjectFromUrl(url, SonOffWrapper.class);
    if (sonOffWrapper == null)
    {
      Logger.error("couldn't read Tasmota status from url {}", url);
      return false;
    }

    resultFields.addAll(sonOffWrapper.getResultFields(specification.getDevicefields()));
    return true;
  }

  @Override protected void createTables()
  {
    tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
    // there can be multiple sensor datas; extract each sensor data into own table entry
    if (sonOffWrapper != null && sonOffWrapper.statusSNS != null)
    {
      for (Map.Entry<String, SensorValue> map : sonOffWrapper.statusSNS.sensors.entrySet())
      {
        List<ResultField> sensorResultField = new ArrayList<>();
        SensorValue sensorValue = map.getValue();
        for (DeviceField deviceField : specification.getDevicefields())
        {
          ResultField field = sensorValue.getResultField(deviceField);
          if (field != null)
          {
            sensorResultField.add(field);
          }
        }
        if (!sensorResultField.isEmpty())
        {
          tables.addAll(exportTables.convert(sensorResultField, specification.getDatabasefields()));
        }
      }
    }
  }

}
