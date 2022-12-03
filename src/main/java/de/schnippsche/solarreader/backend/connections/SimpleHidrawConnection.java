package de.schnippsche.solarreader.backend.connections;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.backend.utils.QCommand;
import org.hid4java.*;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SimpleHidrawConnection implements Connection<String, QCommand>
{
  protected static final Byte STARTBYTE = '(';
  protected static final Byte ENDBYTE = '\r';
  private static HidServices hidServices = null;
  private final ConfigDevice configDevice;
  private HidDevice hidDevice;

  public SimpleHidrawConnection(ConfigDevice configDevice)
  {
    if (hidServices == null)
    {
      setHidServices();
    }
    this.configDevice = configDevice;
  }

  private synchronized void setHidServices()
  {
    Logger.info("create Hid service once...");
    HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
    hidServicesSpecification.setAutoStart(true);
    hidServicesSpecification.setAutoShutdown(true);
    hidServicesSpecification.setScanInterval(1000);
    hidServicesSpecification.setPauseInterval(5000);
    hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);
    hidServices = HidManager.getHidServices(hidServicesSpecification);
  }

  /**
   * try to read some bytes into the byte buffer until stop byte found or timeout
   *
   * @param byteBuffer the byte buffer for storing the data
   * @throws InterruptedException if an error occur
   */
  private void readBytes(ByteBuffer byteBuffer) throws InterruptedException
  {
    boolean ended = false;
    long endTime = System.currentTimeMillis() + 5 * 1000; // maximum of 5 seconds for whole payload
    do
    {
      TimeUnit.MILLISECONDS.sleep(100);
      Byte[] readBytes = hidDevice.read();
      Logger.debug("read {} bytes", readBytes.length);
      for (Byte readByte : readBytes)
      {
        if (STARTBYTE.equals(readByte))
        {
          byteBuffer.clear();
        } else if (ENDBYTE.equals(readByte))
        {
          ended = true;
          break;
        } else
        {
          byteBuffer.put(readByte);
        }
      }
    } while (!ended && (System.currentTimeMillis() < endTime));
  }

  /**
   * get all current connected hidraw devices
   *
   * @return List of hidraw devices or empty list
   */
  public List<Pair> getAvailableHidraws()
  {
    hidServices.scan();
    List<Pair> result = new ArrayList<>();
    List<HidDevice> devices = hidServices.getAttachedHidDevices();
    for (HidDevice device : devices)
    {
      result.add(new Pair(device.getPath(), getReadableInfo(device)));
    }
    return result;
  }

  /**
   * search all connected hid devices and compares to path
   *
   * @param path the path to search for
   * @return HidDevice if found or null if nothing found
   */
  public HidDevice getDeviceFromPath(String path)
  {
    for (HidDevice device : hidServices.getAttachedHidDevices())
    {
      if (Objects.equals(device.getPath(), path))
      {
        return device;
      }
    }
    return null;
  }

  /**
   * build a human readable info for a hidraw device
   *
   * @param hidDevice the device
   * @return String with human readable information about the device
   */
  public String getReadableInfo(HidDevice hidDevice)
  {
    List<String> infos = new ArrayList<>();
    if (hidDevice.getManufacturer() != null)
    {
      infos.add("Manufacturer:" + hidDevice.getManufacturer());
    }
    if (hidDevice.getProduct() != null)
    {
      infos.add("Product:" + hidDevice.getProduct());
    }
    infos.add("ProductId:" + hidDevice.getProductId());
    infos.add("VendorId:" + hidDevice.getVendorId());
    if (hidDevice.getSerialNumber() != null)
    {
      infos.add("Serialnumber:" + hidDevice.getSerialNumber());
    }
    infos.add("Path:" + hidDevice.getPath());
    return String.join(", ", infos);
  }

  /**
   * get a specific hidraw device.
   * First look only at some specific informations like vendorId, productId and serial.
   * If multiple devices found search for the path from the configuration.
   *
   * @param configDevice the configuration informations
   * @return HidDevice if found or null if nothing found
   */
  private HidDevice getSpecifiedHidDevice(ConfigDevice configDevice)
  {
    final String usbPath = configDevice.getParam(ConfigDeviceField.HIDRAW_PATH);
    final int vendorId = configDevice.getIntParamOrDefault(ConfigDeviceField.HIDRAW_VENDOR_ID, 0);
    final int productId = configDevice.getIntParamOrDefault(ConfigDeviceField.HIDRAW_PRODUCT_ID, 0);
    final String serial = configDevice.getParam(ConfigDeviceField.HIDRAW_SERIAL);
    // iterate over all connected hidraw deices
    List<HidDevice> candidates =
      hidServices.getAttachedHidDevices().stream().filter(d -> Objects.equals(d.getProductId(), productId))
                 .filter(d -> Objects.equals(d.getVendorId(), vendorId))
                 .filter(d -> Objects.equals(d.getSerialNumber(), serial)).collect(Collectors.toList());
    if (candidates.isEmpty())
    {
      Logger.error("device not attached or found, productId={}, vendorId={}, serial={}", productId, vendorId, serial);
      return null;
    }

    if (candidates.size() == 1)
    {
      return candidates.get(0);
    }
    // some devices are not unique, so we need the path too
    candidates = hidServices.getAttachedHidDevices().stream().filter(d -> Objects.equals(d.getProductId(), productId))
                            .filter(d -> Objects.equals(d.getVendorId(), vendorId))
                            .filter(d -> Objects.equals(d.getSerialNumber(), serial))
                            .filter(d -> Objects.equals(d.getPath(), usbPath)).collect(Collectors.toList());
    if (candidates.size() == 1)
    {
      return candidates.get(0);
    }
    // nothing found
    Logger.error("device not attached or found, productId={}, vendorId={}, serial={}, path={}", productId, vendorId, serial, usbPath);
    return null;
  }

  /**
   * read a line from a hidraw device
   *
   * @return string containing only the relevant data without start marker, crc and end marker or null if an error occurs
   */
  public String readLine()
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(256);
    try
    {
      readBytes(byteBuffer);
    } catch (InterruptedException e)
    {
      Logger.info("Thread interrupt");
      Thread.currentThread().interrupt();
      return null;
    } catch (Exception e)
    {
      Logger.error("Error:{}", e.getMessage(), e);
      return null;
    }
    byte[] result = new byte[byteBuffer.position()];
    byteBuffer.rewind();
    byteBuffer.get(result);
    if (result.length > 2)
    {
      // remove last 2 crc bytes
      return new String(result, 0, result.length - 2, StandardCharsets.ISO_8859_1);
    }
    Logger.warn("empty or invalid result size {}", result.length);
    return null;
  }

  /**
   * opens a hidraw device
   *
   * @return true if successful or false
   */
  public boolean open()
  {
    hidDevice = getSpecifiedHidDevice(configDevice);
    if (hidDevice != null)
    {
      Logger.debug("try to open device");
      if (!hidDevice.open())
      {
        Logger.error("couldn't open connection to {}:{}", hidDevice, hidDevice.getLastErrorMessage());
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * close a hidraw device
   */
  public void close()
  {
    hidDevice.close();
  }

  /**
   * sends the command like QMOD to the HidDevcie and get the result as String
   *
   * @param command the command for sending to device
   * @return String or null if something is wrong
   */
  public String send(QCommand command)
  {
    Logger.info("send command {} to device ...", command.getCommand());
    int bytesWritten = hidDevice.write(command.getByteCommand(), 64, (byte) 0x00);
    if (bytesWritten < 0)
    {
      Logger.error("could not write, reason = {}", hidDevice.getLastErrorMessage());
      return null;
    }
    String line = readLine();
    Logger.debug("read line {}", line);
    return line;
  }

}
