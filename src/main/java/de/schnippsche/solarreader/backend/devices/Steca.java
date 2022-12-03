package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;

import java.math.BigDecimal;

public class Steca extends SimpleUsbQmod
{

  // Steca Protokoll
  public Steca(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void correctValues()
  {
    ResultField currentValueField = getValidResultField("PV_Ladeleistung");
    if (currentValueField != null)
    {
      BigDecimal factor = dayValueWrapper.getFactor(getConfigDevice().getActivity());
      BigDecimal wattTotal = dayValueWrapper.addValue(currentValueField, factor);
      setWattTotalToday(wattTotal);
      resultFields.add(new ResultField("Tagesenergie", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotal));
    }
  }

}
