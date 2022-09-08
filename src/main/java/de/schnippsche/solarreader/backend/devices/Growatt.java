package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.*;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class Growatt extends AbstractLockedDevice
{
  private ModbusWrapper modbusWrapper;
  private String firmware;
  private DeviceField statusField;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Growatt(ConfigDevice configDevice)
  {
    super(configDevice);
    firmware = null;
    specification = null;
    statusField = null;
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
  }

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
      checkFirmware();
      if (!checkStatus())
      {
        return false;
      }

      resultFields.addAll(modbusWrapper.readFieldBlocks(deviceFieldBlocks));

      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
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

  private boolean checkStatus()
  {
    Logger.info("Check device status");
    if (statusField == null)
    {
      return true;
    }
    ResultField result = modbusWrapper.readField(statusField);
    BigDecimal status = result.getNumericValue();
    if (status.intValue() == 0)
    {
      Logger.warn("device has waiting status!");
      return false;
    }
    if (status.intValue() == 3)
    {
      Logger.warn("device has error status!");
      return false;
    }
    resultFields.add(result);
    return true;
  }

  private void checkFirmware() throws ModbusException
  {
    if (firmware != null)
    {
      return;
    }
    Logger.info("check Firmware");
    firmware = modbusWrapper.readRegisterAsString(3, 9, 3);
    String fw = (firmware + "  ").substring(0, 2);
    int cacheSize = 96;
    byte firmwareVersion;
    switch (fw)
    {
      case "  ":
      case "G.":
      case "DK":
        firmwareVersion = 1;
        cacheSize = 32;
        break;
      case "AL":
      case "GH":
      case "DH":
      case "DL":
        firmwareVersion = 2;
        break;
      case "RA":
      case "YA":
      case "TJ":
        firmwareVersion = 3;
        break;
      case "04": // 040
      case "06": // 067
        firmwareVersion = 4;
        cacheSize = 32;
        break;
      default:
        Logger.info("unknown firmware version '{}'", firmware);
        specification = new Specification();
        specification.setDevicefields(Collections.emptyList());
        return;
    }
    Logger.info("firmware {} = version {}", fw, firmwareVersion);
    specification = jsonTool.readSpecification("growatt_v" + firmwareVersion);
    // Exctract status Field from device List
    statusField = getDeviceField("Status");
    if (statusField != null)
    {
      specification.getDevicefields().remove(statusField);
    }
    // Convert into blocks for better read performance
    deviceFieldBlocks = new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), cacheSize);
  }

  @Override protected void correctValues()
  {
    resultFields.add(new ResultField("firmware", ResultFieldStatus.VALID, FieldType.STRING, firmware));
    // Firmware DK: Temperatur falsch!
    if ("DK".equals(firmware.substring(0, 2)))
    {
      ResultField temp = getValidResultField("Temperatur");
      if (temp != null)
      {
        temp.setValue(temp.getNumericValue().multiply(new BigDecimal("0.1")));
      }
    }

    ResultField field = getValidResultField("Bat_Watt");
    if (field != null)
    {
      BigDecimal watt = field.getNumericValue();
      if (watt.compareTo(BigDecimal.ZERO) > 0)
      {
        resultFields.add(new ResultField("Entladung", ResultFieldStatus.VALID, watt));
        resultFields.add(new ResultField("Ladung", ResultFieldStatus.VALID, BigDecimal.ZERO));
      } else
      {
        resultFields.add(new ResultField("Entladung", ResultFieldStatus.VALID, BigDecimal.ZERO));
        resultFields.add(new ResultField("Ladung", ResultFieldStatus.VALID, watt.abs()));
      }
    }
    setWattTotalResultField("WattStundenGesamtHeute");
  }

  @Override protected void createTables()
  {
    this.tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

}
