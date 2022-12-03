package de.schnippsche.solarreader.backend.fields;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import de.schnippsche.solarreader.backend.utils.NumericHelper;

import java.util.ArrayList;
import java.util.List;

public class DeviceFieldBlock
{
  private final int maxBlockSize;
  private final ArrayList<DeviceField> originalDeviceFields;
  private final DeviceField blockDeviceField;
  private int sum;
  private int firstOffset;

  public DeviceFieldBlock(int maxBlockSize)
  {
    this.maxBlockSize = maxBlockSize;
    this.originalDeviceFields = new ArrayList<>();
    this.blockDeviceField = new DeviceField();
    sum = 0;
    firstOffset = 0;
  }

  public boolean addDeviceField(DeviceField deviceField)
  {
    if (originalDeviceFields.isEmpty())
    {
      blockDeviceField.setRegister(deviceField.getRegister());
      blockDeviceField.setCount(deviceField.getCount());
      blockDeviceField.setOffset(deviceField.getOffset());
      originalDeviceFields.add(deviceField);
      sum += deviceField.getCount();
      firstOffset = deviceField.getOffset();
      return true;
    }

    if (deviceField.getRegister()
                   .equals(blockDeviceField.getRegister()) && deviceField.getOffset() == blockDeviceField.getOffset() + blockDeviceField.getCount() && blockDeviceField.getCount() + deviceField.getCount() <= maxBlockSize)
    {
      originalDeviceFields.add(deviceField);
      blockDeviceField.setCount(blockDeviceField.getCount() + deviceField.getCount());
      sum += deviceField.getCount();
      return true;
    }
    return false;
  }

  public List<DeviceField> getOriginalDeviceFields()
  {
    return originalDeviceFields;
  }

  public DeviceField getBlockDeviceField()
  {
    return blockDeviceField;
  }
  /**
   * converts the inputregister array from the block into the devicefields resultvalue
   *
   * @param inputRegister the array with all inputregisters
   * @param numericHelper NumericHelper instance
   * @return List of result fields for each device field in that block
   */
  public List<ResultField> convertToResultFields(InputRegister[] inputRegister, NumericHelper numericHelper)
  {
    List<ResultField> result = new ArrayList<>();
    int counter;
    for (DeviceField deviceField : getOriginalDeviceFields())
    {
      int byteCount = deviceField.getCount() * 2;
      counter = 0;
      byte[] bytes = new byte[byteCount];
      int first = deviceField.getOffset() - firstOffset;
      int last = first + deviceField.getCount();
      for (int reg = first; reg < last; reg++)
      {
        byte[] irb = inputRegister[reg].toBytes();
        bytes[counter++] = irb[0];
        bytes[counter++] = irb[1];
      }
      try
      {
        Object value = numericHelper.convertByteArray(bytes, deviceField.getType());
        result.add(deviceField.createResultField(value));
      }
      catch (NumberFormatException e)
      {
        result.add(new ResultField(deviceField, ResultFieldStatus.INVALIDNUMBER, null));
      }
    }

    return result;
  }

  @Override public String toString()
  {
    return "DeviceFieldBlock{" + "maxBlockSize=" + maxBlockSize + ", size=" + sum + ", originalDeviceFields=" + originalDeviceFields + ", blockDeviceField=" + blockDeviceField + '}';
  }

}
