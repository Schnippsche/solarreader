package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import org.tinylog.Logger;

import java.math.BigDecimal;

public class Shelly extends AbstractDevice
{
  protected final DayValueWrapper dayValueWrapper;
  private String statusUrl;
  private String shellyUrl;

  public Shelly(ConfigDevice configDevice)
  {
    super(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    statusUrl =
      String.format("http://%s:%s/Status", getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost"), getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80"));
    shellyUrl =
      String.format("http://%s:%s/shelly", getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost"), getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80"));
  }

  @Override protected void correctValues()
  {
    BigDecimal wattTotal = BigDecimal.ZERO;
    for (int i = 0; i < 3; i++)
    {
      ResultField wirkleistung = getValidResultField("emeters_" + i + "_power");
      if (wirkleistung != null)
      {
        BigDecimal factor = dayValueWrapper.getFactor(getConfigDevice().getActivity());
        BigDecimal watt = dayValueWrapper.addValue(wirkleistung, factor);
        wattTotal = wattTotal.add(watt);
      }
    }
    setWattTotalToday(wattTotal);
    resultFields.add(new ResultField("WattstundenGesamtHeute", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotal));
  }

  @Override protected boolean readDeviceValues()
  {

    Logger.info("try to connect to " + shellyUrl);
    resultFields.clear();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(shellyUrl));
    if (resultFields.isEmpty())
    {
      Logger.error("couldn't read Shelly info from url {}", statusUrl);
      return false;
    }
    Logger.info("try to connect to " + statusUrl);
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(statusUrl));

    return true;
  }

}
