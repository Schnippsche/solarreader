package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.AdditionalParameter;
import de.schnippsche.solarreader.backend.utils.DeviceFieldCompressor;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;

import java.util.List;

/**
 * class for similar modbus devices with no extra code
 */
public class SimpleModbus extends AbstractLockedDevice
{

  protected ModbusWrapper modbusWrapper;
  protected List<DeviceFieldBlock> deviceFieldBlocks;

  public SimpleModbus(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    int blockSize = specification.getAdditionalParameterAsInteger(AdditionalParameter.BLOCK_SIZE, 32);
    int sleepMilliseconds = specification.getAdditionalParameterAsInteger(AdditionalParameter.SLEEP_MILLISECONDS, 0);
    modbusWrapper.setSleepMilliseconds(sleepMilliseconds);
    deviceFieldBlocks =
      new DeviceFieldCompressor().convertDeviceFieldsIntoBlocks(specification.getDevicefields(), blockSize);
  }

  @Override public boolean checkConnection()
  {
    initialize();
    return modbusWrapper.checkConnection();
  }

  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllBlocks(deviceFieldBlocks);
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
  }

}
