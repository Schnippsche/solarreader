package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;

import java.math.BigDecimal;

public class EasunSmg extends SimpleModbus
{
  protected final DayValueWrapper dayValueWrapper;

  public EasunSmg(ConfigDevice configDevice)
  {
    super(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
  }

  @Override protected void correctValues()
  {
    ResultField currentValueField = getValidResultField("Tagesenergie");
    if (currentValueField != null)
    {
      BigDecimal factor = dayValueWrapper.getFactor(getConfigDevice().getActivity());
      BigDecimal wattTotal = dayValueWrapper.addValue(currentValueField, factor);
      setWattTotalToday(wattTotal);
    }
  }

}
