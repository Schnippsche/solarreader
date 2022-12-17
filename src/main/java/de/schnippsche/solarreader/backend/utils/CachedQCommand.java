package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

import java.util.List;

/**
 * Class for all Q Commands such as QPIGS, QMOD etc.
 */
public class CachedQCommand extends QCommand
{
  private final boolean cacheable;
  private List<ResultField> cachedResultFields;
  private List<DeviceField> deviceFields;

  public CachedQCommand(String command, boolean cacheable)
  {
    super(command);
    this.cacheable = cacheable;
  }

  public List<ResultField> getCachedResultFields()
  {
    return cachedResultFields;
  }

  public void setCachedResultFields(List<ResultField> cachedResultFields)
  {
    this.cachedResultFields = cachedResultFields;
  }

  public List<DeviceField> getDeviceFields()
  {
    return deviceFields;
  }

  public void setDeviceFields(List<DeviceField> deviceFields)
  {
    this.deviceFields = deviceFields;
  }

  public boolean isCached()
  {
    return cacheable && cachedResultFields != null;
  }

  public boolean isCacheable()
  {
    return cacheable;
  }

}
