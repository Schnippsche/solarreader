package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.util.List;

public class Huawei_M012 extends SimpleModbus
{
  private ModbusWrapper modbusWrapper;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Huawei_M012(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  // 62 = Huawei Wechselrichter mit sDongle [ -M0, -M1, -M2 Modelle]

}
