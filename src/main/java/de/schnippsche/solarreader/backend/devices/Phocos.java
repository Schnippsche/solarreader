package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.util.List;

public class Phocos extends AbstractLockedDevice
{
  private ModbusWrapper modbusWrapper;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public Phocos(ConfigDevice configDevice)
  {
    super(configDevice);
    specification = jsonTool.readSpecification("phocos");
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    deviceFieldBlocks = new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), 32);
  }
  //  40 = Phocos PH1800 Wechselrichter
  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllBlocks(deviceFieldBlocks);
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
  }

  @Override protected void correctValues()
  {
  }

  @Override protected void createTables()
  {
    this.tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

}
