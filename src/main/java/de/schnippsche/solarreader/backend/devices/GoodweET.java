package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import org.tinylog.Logger;

public class GoodweET extends AbstractLockedDevice
{
  private ModbusWrapper modbusWrapper;

  // Holding Register 3 MultipleRegister
  // Input Register = 4

  public GoodweET(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification("goodwe_et");
  }

  // 64 Goodwe Wechselrichter der Serien ET, EH, BH, BT
  @Override protected boolean readLockedDeviceValues()
  {
    AbstractModbusMaster modbusMaster = modbusWrapper.getModbusMaster();
    if (modbusMaster == null)
    {
      return false;
    }

    try
    {
      Logger.info("try to connect to {}", modbusWrapper.getInfoText());
      modbusMaster.connect();
      Logger.info("connected");

      if (!checkDeviceType())
      {
        return false;
      }

      resultFields.addAll(modbusWrapper.readFields(specification.getDevicefields()));
      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
    } catch (ModbusException e)
    {
      Logger.error("can't read from {}", modbusWrapper.getInfoText());
      return false;
    } catch (Exception e)
    {
      Logger.error(e);
      return false;

    } finally
    {
      modbusMaster.disconnect();
      Logger.debug("disconnected from {}", modbusWrapper.getInfoText());
    }

    return true;
  }

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
