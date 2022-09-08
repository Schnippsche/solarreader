package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.DeviceFieldBlock;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * class for compressing small device fields into bigger blocks for better i/o performance
 */
public class DeviceFieldCompressor
{
  /**
   * convert list of devicefields into bigger read blocks for better performance
   *
   * @param deviceFields list of device fields
   * @param maxBlockSize blocksize , depends on device
   * @return list of deviceFieldBlocks
   */
  public List<DeviceFieldBlock> convertDeviceFieldsIntoBlocks(List<DeviceField> deviceFields, int maxBlockSize)
  {
    Logger.info("convert device fields into blocks for better i/o performance; blocksize is {}...", maxBlockSize);
    List<DeviceFieldBlock> blocks = new ArrayList<>();
    Collections.sort(deviceFields);
    DeviceFieldBlock block = new DeviceFieldBlock(maxBlockSize);
    for (DeviceField deviceField : deviceFields)
    {
      if (!block.addDeviceField(deviceField))
      {
        blocks.add(block);
        block = new DeviceFieldBlock(maxBlockSize);
        block.addDeviceField(deviceField);
      }
    }
    if (!block.getOriginalDeviceFields().isEmpty())
    {
      blocks.add(block);
    }
    Logger.info("finished converting, {} devicefields convert into {} blocks!", deviceFields.size(), blocks.size());
    return blocks;
  }

}
