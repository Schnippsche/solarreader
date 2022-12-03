package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;

import java.math.BigDecimal;

public class Mpp30 extends SimpleUsbQmod
{

  public Mpp30(ConfigDevice configDevice)
  {
    super(configDevice);
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
