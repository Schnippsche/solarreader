package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppInfo
{
  private String deviceInfoVersion;
  private String lastUpdate;
  private List<DeviceInfo> deviceInfos;

  public AppInfo()
  {
    this.deviceInfos = new ArrayList<>();
    this.lastUpdate = LocalDateTime.now().toString();
  }

  public String getDeviceInfoVersion()
  {
    return deviceInfoVersion;
  }

  public void setDeviceInfoVersion(String deviceInfoVersion)
  {
    this.deviceInfoVersion = deviceInfoVersion;
  }

  public String getLastUpdate()
  {
    return lastUpdate;
  }

  public void setLastUpdate(String lastUpdate)
  {
    this.lastUpdate = lastUpdate;
  }

  public List<DeviceInfo> getDeviceInfos()
  {
    return deviceInfos;
  }

  public void setDeviceInfos(List<DeviceInfo> deviceInfos)
  {
    this.deviceInfos = deviceInfos;
  }

  public HtmlOptionList getDeviceOptionList()
  {
    List<Pair> pairs = new ArrayList<>();
    Comparator<DeviceInfo> comparator = Comparator.comparing(DeviceInfo::getDeviceName);
    deviceInfos.sort(comparator);
    for (DeviceInfo deviceInfo : deviceInfos)
    {
      pairs.add(new Pair(deviceInfo.getUuid(), deviceInfo.getDeviceName()));
    }
    return new HtmlOptionList(pairs);
  }

  public DeviceInfo getDeviceInfo(String uuid)
  {
    for (DeviceInfo deviceInfo : deviceInfos)
    {
      if (deviceInfo.getUuid().equals(uuid))
      {
        return deviceInfo;
      }
    }
    return new DeviceInfo();
  }

}
