package de.schnippsche.solarreader.backend.devices.abstracts;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;

public abstract class AbstractLockedDevice extends AbstractDevice
{
  protected AbstractLockedDevice(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected boolean readDeviceValues()
  {
    boolean result;
    synchronized (Config.getInstance().getLockObject(getConfigDevice()))
    {
      result = readLockedDeviceValues();
    }
    return result;
  }

  protected abstract boolean readLockedDeviceValues();

}
