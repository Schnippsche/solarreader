package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import org.tinylog.Logger;

import java.math.BigDecimal;

public class Tasmota extends AbstractDevice
{
  private String url;

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
    resultFields.clear();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(url));
    return !resultFields.isEmpty();
  }

  @Override protected void correctValues()
  {
    setBooleanToNumber(getValidResultField("StatusSTS_POWER"));
    setBooleanToNumber(getValidResultField("StatusSTS_POWER1"));
    setBooleanToNumber(getValidResultField("StatusSTS_POWER2"));
  }

  private void setBooleanToNumber(ResultField resultField)
  {
    if (resultField != null)
    {
      resultField.setValue("ON".equals(resultField.getValue()) ? BigDecimal.ONE : BigDecimal.ZERO);
    }
  }

}
