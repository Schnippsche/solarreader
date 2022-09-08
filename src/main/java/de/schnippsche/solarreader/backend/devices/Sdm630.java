package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.util.List;

public class Sdm630 extends AbstractLockedDevice
{
  private ModbusWrapper modbusWrapper;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Sdm630(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification("sdm630");
    deviceFieldBlocks = new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), 32);
  }

  // 34 = SDM630  Energy Meter (RS485 Anschluss) BAUDRATE = 19200!
  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllBlocks(deviceFieldBlocks);
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
  }

  @Override protected void correctValues()
  {
    // nothing
  }

  @Override protected void createTables()
  {
    this.tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

}
