package de.schnippsche.solarreader.backend.utils;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.connections.Connection;
import de.schnippsche.solarreader.backend.connections.SimpleHidrawConnection;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import org.tinylog.Logger;

public class UsbWrapper
{

  private final ConfigDevice configDevice;
  private Connection<String, QCommand> connection;

  /**
   * Constructor for Wrapper
   * USB can be read with serial to usb Converter or with usb cable (hidraw)
   *
   * @param configDevice the new instance
   */
  public UsbWrapper(ConfigDevice configDevice)
  {
    this.configDevice = configDevice;
    this.connection = null;
  }

  public boolean open()
  {
    if (configDevice.containsField(ConfigDeviceField.COM_PORT) && !configDevice.getParam(ConfigDeviceField.COM_PORT)
                                                                               .isEmpty())
    {
      connection = new SimpleSerialConnection(configDevice);
      return connection.open();
    }
    if (configDevice.containsField(ConfigDeviceField.HIDRAW_PATH)
        && !configDevice.getParam(ConfigDeviceField.HIDRAW_PATH).isEmpty())
    {
      connection = new SimpleHidrawConnection(configDevice);
      return connection.open();
    }
    Logger.error("couldn't find COM Port or HIDRAW path in configDevice: {}", configDevice);
    return false;
  }

  public void close()
  {
    if (connection != null)
    {
      connection.close();
    }
  }

  public String send(QCommand command)
  {
    if (connection == null)
    {
      return null;
    }
    Logger.info("send command {} to device ...", command.getCommand());
    return connection.send(command);
  }

}
