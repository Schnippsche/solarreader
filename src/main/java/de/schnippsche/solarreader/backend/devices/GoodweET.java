package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import org.tinylog.Logger;

public class GoodweET extends SimpleModbus
{
  private ModbusWrapper modbusWrapper;

  // Holding Register 3 MultipleRegister
  // Input Register = 4

  public GoodweET(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  // 64 Goodwe Wechselrichter der Serien ET, EH, BH, BT

  private boolean checkDeviceType()
  {
    Logger.info("Check device type");
    DeviceField deviceField = getDeviceField("Device_Type");
    if (deviceField == null)
    {
      return true;
    }
    ResultField result = modbusWrapper.readField(deviceField);
    String produkt = String.valueOf(result.getValue()).trim();
    if (produkt.isEmpty())
    {
      Logger.warn("device has no valid data!");
      return false;
    }
    // remove field because it is already readed
    specification.getDevicefields().remove(deviceField);
    resultFields.add(result);

    return true;
  }

  @Override protected void correctValues()
  {
    ResultField errorBit = getValidResultField("FehlerCode");
    if (errorBit != null)
    {
      int errorValue = errorBit.getNumericValue().intValue();
      errorBit.setValue(numericHelper.getBinary16String(errorValue));
      errorBit.setType(FieldType.STRING);
    }
    setWattTotalResultField("WattStundenGesamtHeute");
  }

}
