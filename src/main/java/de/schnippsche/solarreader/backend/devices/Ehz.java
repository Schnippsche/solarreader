package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.MessageBeginListener;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ehz extends AbstractLockedDevice
{
  private String usbDevice;
  private SimpleSerialConnection simpleSerialConnection;

  public Ehz(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    usbDevice = getConfigDevice().getParam(ConfigDeviceField.COM_PORT);
    this.simpleSerialConnection = new SimpleSerialConnection(getConfigDevice());
  }

  @Override protected boolean readLockedDeviceValues()
  {
    try
    {
      Logger.debug("usbDevice:", usbDevice);
      if (!simpleSerialConnection.open())
      {
        return false;
      }
      MessageBeginListener beginListener = new MessageBeginListener();
      simpleSerialConnection.addDataListener(beginListener);
      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() - start < 5000 && !beginListener.isOk())
      {
        Thread.sleep(200);
      }
      if (!beginListener.isOk())
      {
        Logger.error("Not enough data");
        return false;
      }
      String receive = beginListener.getHexData();
      if (receive == null || receive.isEmpty())
      {
        Logger.error("data is empty!");
        return false;
      }
      Logger.debug("Receive:{}", receive);
      if (receive.length() < 200)
      {
        Logger.error("not enough data, only {} bytes read", receive.length());
        return false;
      }

      extractFields(receive);
      return true;
    } catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    } catch (Exception e)
    {
      Logger.error("error while reading {}: {}", usbDevice, e);
      return false;
    } finally
    {
      simpleSerialConnection.removeDataListener();
      simpleSerialConnection.close();
    }
    return false;

  }

  private void extractFields(String receive)
  {
    for (DeviceField deviceField : this.specification.getDevicefields())
    {
      Logger.debug(deviceField);
      Pattern pattern = Pattern.compile(deviceField.getUnit());
      Matcher matcher = pattern.matcher(receive);
      BigDecimal factor = deviceField.getFactor();
      int offset = deviceField.getOffset() - 1;
      if (matcher.find())
      {
        int value = Integer.parseUnsignedInt(matcher.group(offset).trim(), 16);
        BigDecimal result = BigDecimal.valueOf(value);
        if (factor != null)
        {
          result = result.multiply(factor);
        }
        Logger.debug("Value={}", result);
        resultFields.add(new ResultField(deviceField, result));
      } else
      {
        Logger.debug("not found!");
      }
    }
  }

}
