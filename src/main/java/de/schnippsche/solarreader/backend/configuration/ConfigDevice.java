package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.automation.Command;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.Activity;

import java.util.*;

public class ConfigDevice
{
  private final String uuid;
  private final transient List<ResultField> cachedResultFields;
  private ConfigExport configExport;
  private HashMap<ConfigDeviceField, String> params;
  private String deviceInfoId;
  private String deviceName;
  private String deviceClass;
  private String deviceSpecification;
  private String description;
  private Activity activity;
  private transient List<Command> automationCommands;

  public ConfigDevice()
  {
    uuid = UUID.randomUUID().toString();
    deviceName = "";
    deviceClass = "";
    deviceInfoId = "";
    description = "";
    deviceSpecification = null;
    activity = new Activity();
    params = new HashMap<>(); // // do not use enum map because converting to json sucks!
    configExport = new ConfigExport();
    automationCommands = null;
    cachedResultFields = new ArrayList<>();
  }

  public ConfigDevice(DeviceInfo deviceInfo)
  {
    uuid = UUID.randomUUID().toString();
    deviceName = deviceInfo.getDeviceName();
    deviceClass = deviceInfo.getDeviceClass();
    setParams(deviceInfo.getDefaults());
    deviceInfoId = deviceInfo.getUuid();
    configExport = new ConfigExport();
    deviceSpecification = deviceInfo.getDeviceSpecification();
    cachedResultFields = new ArrayList<>();
  }

  public ConfigDevice(ConfigDevice other)
  {
    uuid = other.uuid;
    deviceName = other.deviceName;
    deviceClass = other.deviceClass;
    deviceInfoId = other.deviceInfoId;
    description = other.description;
    activity = new Activity(other.activity);
    configExport = new ConfigExport(other.getConfigExport());
    deviceSpecification = other.deviceSpecification;
    setParams(other.params);
    cachedResultFields = other.cachedResultFields;
  }

  public void changeValues(ConfigDevice newConfigDevice)
  {
    setParams(newConfigDevice.getParams());
    setDescription(newConfigDevice.getDescription());
    setConfigExport(newConfigDevice.getConfigExport());
  }

  public boolean containsField(ConfigDeviceField configDeviceField)
  {
    return params.containsKey(configDeviceField);
  }

  public String getParam(ConfigDeviceField configDeviceField)
  {
    return params.get(configDeviceField);
  }

  public void setParam(ConfigDeviceField configDeviceField, String value)
  {
    if (value != null)
    {
      params.put(configDeviceField, value);
    }
  }

  public Map<ConfigDeviceField, String> getParams()
  {
    return params;
  }

  public void setParams(Map<ConfigDeviceField, String> params)
  {
    // Make a copy of the map, not a reference
    this.params = new HashMap<>(params);
  }

  public String getParamOrDefault(ConfigDeviceField configDeviceField, String defaultValue)
  {
    return params.getOrDefault(configDeviceField, defaultValue);
  }

  public List<ResultField> getCachedResultFields()
  {
    return cachedResultFields;
  }

  public ResultField getCachedResultField(String fieldName)
  {
    for (ResultField resultField : cachedResultFields)
    {
      if (resultField.getName().equals(fieldName))
      {
        return resultField;
      }
    }
    return null;
  }

  public int getIntParamOrDefault(ConfigDeviceField configDeviceField, int defaultValue)
  {
    try
    {
      return Integer.parseInt(params.getOrDefault(configDeviceField, String.valueOf(defaultValue)));
    } catch (NumberFormatException exception)
    {
      return defaultValue;
    }
  }

  public boolean isParamEnabled(ConfigDeviceField configDeviceField)
  {
    return Boolean.parseBoolean(params.getOrDefault(configDeviceField, "FALSE"));
  }

  public String getDeviceClass()
  {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass)
  {
    this.deviceClass = deviceClass;
  }

  public String getDeviceName()
  {
    return deviceName;
  }

  public void setDeviceName(String deviceName)
  {
    this.deviceName = deviceName;
  }

  public Activity getActivity()
  {
    return activity;
  }

  public void setActivity(Activity activity)
  {
    this.activity = activity;
  }

  public String getUuid()
  {
    return uuid;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDeviceInfoId()
  {
    return deviceInfoId;
  }

  public void setDeviceInfoId(String deviceInfoId)
  {
    this.deviceInfoId = deviceInfoId;
  }

  public String getDeviceSpecification()
  {
    return deviceSpecification;
  }

  public void setDeviceSpecification(String deviceSpecification)
  {
    this.deviceSpecification = deviceSpecification;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    final ConfigDevice that = (ConfigDevice) o;
    return uuid.equals(that.uuid);
  }

  public List<Command> getAutomationCommands()
  {
    return automationCommands;
  }

  public void setAutomationCommands(List<Command> automationCommands)
  {
    this.automationCommands = automationCommands;
  }

  public ConfigExport getConfigExport()
  {
    return configExport;
  }

  public void setConfigExport(ConfigExport configExport)
  {
    this.configExport = configExport;
  }

  @Override public int hashCode()
  {
    return Objects.hash(uuid);
  }

  @Override public String toString()
  {
    return String.format("ConfigDevice{uuid='%s', configExport=%s, params=%s, deviceName='%s', deviceClass='%s', description='%s', activity=%s}", uuid, configExport, params, deviceName, deviceClass, description, activity);
  }

}
