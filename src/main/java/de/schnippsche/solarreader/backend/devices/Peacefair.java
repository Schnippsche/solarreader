package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import org.tinylog.Logger;

import java.util.List;

public class Peacefair extends SimpleModbus
{
  public Peacefair(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void sendCheckedCommandsToDevice(List<ExpiringCommand> commands)
  {
    SimpleSerialConnection serialConnection = new SimpleSerialConnection(getConfigDevice());
    int deviceID = getConfigDevice().getIntParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, 1);
    Logger.debug("connect");
    serialConnection.open();
    // there is only one command allowed
    byte[] bytes = new byte[]{(byte) deviceID, 0x42, (byte) 0x80, 0x11};
    serialConnection.send(bytes);
    serialConnection.close();
  }

}
