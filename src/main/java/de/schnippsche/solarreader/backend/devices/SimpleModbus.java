package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.util.List;
/**
 * class for similar modbus devices with no extra code
 */
public class SimpleModbus extends AbstractLockedDevice
{

  private ModbusWrapper modbusWrapper;
  private List<DeviceFieldBlock> deviceFieldBlocks;

  public SimpleModbus(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    deviceFieldBlocks = new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), 32);
  }

  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllBlocks(deviceFieldBlocks);
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
  }

}
