package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.util.List;

public class Fronius extends AbstractDevice
{
  private static final int MODBUS_CACHE_SIZE = 64;
  private ModbusWrapper modbusWrapper;
  private Integer offset;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Fronius(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    offset = null;
  }

  //  Solaredge Sunspec
  //  @Override
  protected boolean readDeviceValues()
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
      checkOffsetAndModelOnFirstRun();
      if (offset == null)
      {
        return false;
      }

      resultFields.addAll(modbusWrapper.readFieldBlocks(deviceFieldBlocks));
      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.info(field);
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

  @Override public boolean checkConnection()
  {
    return modbusWrapper.checkConnection();
  }

  private void checkOffsetAndModelOnFirstRun() throws ModbusException
  {
    // do it only once
    if (offset != null)
    {
      return;
    }

    if ("SunS".equals(modbusWrapper.readRegisterAsString(3, 40000, 2)))
    {
      offset = 40002;
      Logger.info("Sunspec ID found at offset 40000");
    } else if ("SunS".equals(modbusWrapper.readRegisterAsString(3, 40001, 2)))
    {
      offset = 40003;
      Logger.info("Sunspec ID found at offset 40001");
    } else
    {
      Logger.error("No Sunspec ID found");
    }
    // Iterate over all register
    specification = new Specification();
    int blockId;
    int blockLen;
    do
    {
      blockId = modbusWrapper.readRegisterAsNumber(3, offset, 1, FieldType.U16_BIG_ENDIAN).intValue();
      blockLen = modbusWrapper.readRegisterAsNumber(3, offset + 1, 1, FieldType.U16_BIG_ENDIAN).intValue();
      loadDeviceFieldsForBlock(blockId);
      offset += blockLen + 2;
    } while (blockId != 0xFFFF && blockLen > 0);
    // Convert into blocks for better read performance
    deviceFieldBlocks =
      new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), MODBUS_CACHE_SIZE);
  }

  protected void loadDeviceFieldsForBlock(int blockId)
  {
    switch (blockId)
    {
      case 1:
        readResourceBlock("fronius_common", offset);
        break;
      case 101:
      case 102:
      case 103:
        readResourceBlock("fronius_inverter_int", offset);
        readResourceBlock("fronius", 0);
        break;
      case 111:
      case 112:
      case 113:
        readResourceBlock("fronius_inverter_float", offset);
        readResourceBlock("fronius", 0);
        break;
      case 120:
        readResourceBlock("fronius_nameplate", offset);
        break;
      case 121:
        readResourceBlock("fronius_settings", offset);
        break;
      case 122:
        readResourceBlock("fronius_status", offset);
        break;
      case 123:
        readResourceBlock("fronius_controls", offset);
        break;
      case 124:
        readResourceBlock("fronius_storage", offset);
        break;
      case 160:
        readResourceBlock("fronius_mppt", offset);
        break;
      case 201:
      case 202:
      case 203:
        readResourceBlock("fronius_acmeter_int", offset);
        break;
      case 211:
      case 212:
      case 213:
        readResourceBlock("fronius_acmeter_float", offset);
        break;
      case 302:
        readResourceBlock("fronius_irradiance", offset);
        break;
      case 303:
        readResourceBlock("fronius_backofmodule", offset);
        break;
      case 307:
        readResourceBlock("fronius_meteorological", offset);
        break;
      case 403:
        readResourceBlock("fronius_stringcombiner", offset);
        break;
      case 0xFFFF:
        // End block
        break;
      default:
        Logger.error("unknown sunspec block {}", blockId);
    }

  }

  private void readResourceBlock(String resource, int startOffset)
  {
    Specification spec = jsonTool.readSpecification(resource);
    if (startOffset != 0)
    {
      for (DeviceField deviceField : spec.getDevicefields())
      {
        deviceField.setOffset(deviceField.getOffset() + startOffset);
      }
    }
    specification.getDevicefields().addAll(spec.getDevicefields());
    specification.getDatabasefields().addAll(spec.getDatabasefields());
    specification.getMqttFields().addAll(spec.getMqttFields());
  }

}
