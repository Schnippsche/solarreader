package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import org.tinylog.Logger;

import java.util.List;

public class Huawei_M012 extends AbstractDevice
{
  private ModbusWrapper modbusWrapper;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Huawei_M012(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification("huawei_m012");
    deviceFieldBlocks = new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), 100);
  }

  @Override protected void correctValues()
  {
  }

  // 62 = Huawei Wechselrichter mit sDongle [ -M0, -M1, -M2 Modelle]
  @Override protected boolean readDeviceValues()
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

  @Override protected void createTables()
  {
    this.tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

}
