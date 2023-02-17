package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.AdditionalParameter;
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
  private List<DeviceFieldBlock> deviceFieldBlocks;
  private ResultField firmwareField;

  public Growatt(ConfigDevice configDevice)
  {
    super(configDevice);
    specification = null;
    firmware = null;
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    firmware = null;
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
      if (firmware == null)
      {
        checkFirmware();
      }
      resultFields.addAll(modbusWrapper.readFieldBlocks(deviceFieldBlocks));

      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
    } catch (ModbusException e)
    {
      Logger.error("can't read from {}:{}", modbusWrapper.getInfoText(), e.getMessage());
      return false;
    } catch (Exception e)
    {
      Logger.error(e.getMessage());
      return false;
    } finally
    {
      modbusMaster.disconnect();
      Logger.debug("disconnected from {}", modbusWrapper.getInfoText());
    }

    return true;
  }

  private void checkFirmware() throws ModbusException
  {
    Logger.info("check Firmware");
    firmware = modbusWrapper.readRegisterAsString(3, 9, 3);
    String fw = (firmware + "  ").substring(0, 2);
    byte firmwareVersion;
    firmwareField = new ResultField("firmware", ResultFieldStatus.VALID, FieldType.STRING, firmware);
    switch (fw)
    {
      case "  ":
      case "G.":
      case "DK":
        firmwareVersion = 1;
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
        break;
      default:
        Logger.info("unknown firmware version '{}'", firmware);
        specification = new Specification();
        specification.setDevicefields(Collections.emptyList());
        deviceFieldBlocks = Collections.emptyList();
        return;
    }
    Logger.info("firmware {} = version {}", fw, firmwareVersion);
    specification = jsonTool.readSpecification("growatt_v" + firmwareVersion);
    int blockSize = specification.getAdditionalParameterAsInteger(AdditionalParameter.BLOCK_SIZE, 32);
    int sleepMilliseconds = specification.getAdditionalParameterAsInteger(AdditionalParameter.SLEEP_MILLISECONDS, 0);
    modbusWrapper.setSleepMilliseconds(sleepMilliseconds);
    // Convert into blocks for better read performance
    deviceFieldBlocks =
      new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), blockSize);
  }

  @Override protected void correctValues()
  {
    if (firmwareField != null)
    {
      resultFields.add(firmwareField);
    }
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

  @Override public boolean checkConnection()
  {
    initialize();
    return modbusWrapper.checkConnection();
  }

}
