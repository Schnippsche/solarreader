package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.math.BigDecimal;
import java.util.List;

public class EasunSmg extends AbstractLockedDevice
{
  protected final DayValueWrapper dayValueWrapper;
  private ModbusWrapper modbusWrapper;

  public EasunSmg(ConfigDevice configDevice)
  {
    super(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification("easun_smg");
  }

  // 71 = EASUN SMG II Wechselrichter, BAUDRATE 9600
  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllFields(specification.getDevicefields());
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
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
