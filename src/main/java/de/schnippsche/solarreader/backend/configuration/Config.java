package de.schnippsche.solarreader.backend.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.serializes.gson.LocalDateSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.LocalDateTimeSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.LocalTimeSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.TimeUnitSerializer;
import de.schnippsche.solarreader.backend.utils.JsonTools;
import de.schnippsche.solarreader.backend.worker.ThreadHelper;
import org.tinylog.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Config
{
  public static final String CONFIG_FILE = "solarreaderconfig.json";
  private static final Config instance = new Config();
  private final transient StandardValues standardValues;
  private final transient Hashtable<String, Object> lockObjects; // Synchronized
  private final transient AppInfo appInfo;
  private transient List<AbstractDevice> devices;
  private List<ConfigDatabase> configDatabases;
  private List<ConfigMqtt> configMqtts;
  private ConfigGeneral configGeneral;
  private List<ConfigDevice> configDevices;
  // Solarprognose
  private ConfigSolarprognose configSolarprognose;
  // OpenWeather
  private ConfigOpenWeather configOpenWeather;
  // Awattar
  private ConfigAwattar configAwattar;
  // Messenger
  private ConfigMessenger configMessenger;
  private transient Gson gson;
  private transient boolean mustReloadDevices;

  private Config()
  {
    configDevices = new ArrayList<>();
    configDatabases = new ArrayList<>();
    configMqtts = new ArrayList<>();
    configOpenWeather = new ConfigOpenWeather();
    configSolarprognose = new ConfigSolarprognose();
    configGeneral = new ConfigGeneral();
    configAwattar = new ConfigAwattar();
    configMessenger = new ConfigMessenger();
    standardValues = new StandardValues();
    lockObjects = new Hashtable<>();
    devices = new ArrayList<>();
    gson = getGson();
    appInfo = readAppInfo();
  }

  public static Config getInstance()
  {
    return instance;
  }

  public void readConfiguration()
  {
    try (Reader reader = Files.newBufferedReader(Paths.get(CONFIG_FILE), StandardCharsets.UTF_8))
    {
      Config tmpConfig = getGson().fromJson(reader, Config.class);
      configDevices = tmpConfig.configDevices;
      configOpenWeather = tmpConfig.configOpenWeather;
      configSolarprognose = tmpConfig.configSolarprognose;
      configGeneral = tmpConfig.configGeneral;
      configAwattar = tmpConfig.configAwattar;
      configMessenger = tmpConfig.configMessenger;
      configDatabases = tmpConfig.getConfigDatabases();
      configMqtts = tmpConfig.getConfigMqtts();
      initDevices();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public Gson getGson()
  {
    if (gson == null)
    {
      GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
      builder.registerTypeAdapter(LocalTime.class, new LocalTimeSerializer());
      builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
      builder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
      builder.registerTypeAdapter(TimeUnit.class, new TimeUnitSerializer());
      gson = builder.create();
    }
    return gson;
  }

  public List<AbstractDevice> getDevices()
  {
    return devices;
  }

  public synchronized void removeDevice(ConfigDevice device)
  {
    Logger.info("remove device {}", device.getDescription());
    configDevices.remove(device);
    saveConfiguration();
    setDeviceConfigurationChanged();
  }

  public synchronized void removeDatabase(ConfigDatabase database)
  {
    Logger.info("remove database {}", database.getDescription());
    configDatabases.remove(database);
    saveConfiguration();
  }

  public synchronized void removeMqtt(ConfigMqtt mqtt)
  {
    Logger.info("remove mqtt {}", mqtt.getDescription());
    configMqtts.remove(mqtt);
    saveConfiguration();
  }

  private synchronized void initDevices()
  {
    devices = new ArrayList<>();
    for (ConfigDevice configDevice : configDevices)
    {
      String className = configDevice.getDeviceClass();
      AbstractDevice device = getDeviceFromClassName(configDevice, className);
      if (device != null)
      {
        devices.add(device);
        String lockObjectString = getLockObjectIdentifier(configDevice);
        if (!lockObjectString.isEmpty())
        {
          lockObjects.put(lockObjectString, new Object());
        }
      }
    }
    mustReloadDevices = false;
  }

  private AppInfo readAppInfo()
  {
    InputStream inputStream = JsonTools.class.getClassLoader().getResourceAsStream("deviceinfos.json");
    if (inputStream != null)
    {
      try (Reader reader = new InputStreamReader(inputStream))
      {
        return getGson().fromJson(reader, AppInfo.class);
      } catch (IOException e)
      {
        Logger.error(e);
      }
    }
    return new AppInfo();
  }

  public void checkDeviceConfigurationForUpdates()
  {
    if (mustReloadDevices)
    {
      initDevices();
      ThreadHelper.changedDeviceConfiguration();
    }
  }

  public void setDeviceConfigurationChanged()
  {
    mustReloadDevices = true;
  }

  public AbstractDevice getDeviceFromClassName(ConfigDevice configDevice, String className)
  {
    try
    {
      final Object clazz =
        Class.forName("de.schnippsche.solarreader.backend.devices." + className).getConstructor(ConfigDevice.class)
             .newInstance(configDevice);
      return (AbstractDevice) clazz;
    } catch (Exception e)
    {
      Logger.error(e.getMessage());
    }
    return null;
  }

  public AppInfo getAppInfo()
  {
    return this.appInfo;
  }

  public StandardValues getStandardValues()
  {
    return standardValues;
  }

  public Object getLockObject(ConfigDevice configDevice)
  {
    String lockObject = getLockObjectIdentifier(configDevice);
    return lockObjects.getOrDefault(lockObject, new Object());
  }

  private String getLockObjectIdentifier(ConfigDevice configDevice)
  {

    if (configDevice.containsField(ConfigDeviceField.COM_PORT))
    {
      return configDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, "");
    }
    if (configDevice.containsField(ConfigDeviceField.HIDRAW_PATH))
    {
      return configDevice.getParamOrDefault(ConfigDeviceField.HIDRAW_PATH, "");
    }
    return "";
  }

  public void writeConfiguration() throws IOException
  {
    try (Writer writer = Files.newBufferedWriter(Paths.get(CONFIG_FILE), StandardCharsets.UTF_8))
    {
      writer.write(getGson().toJson(instance));
    }
  }

  private void saveConfiguration()
  {
    try
    {
      writeConfiguration();
    } catch (IOException e)
    {
      Logger.error(e);
    }
  }

  public ConfigGeneral getConfigGeneral()
  {
    return configGeneral;
  }

  public List<ConfigDevice> getConfigDevices()
  {
    return configDevices;
  }

  public ConfigSolarprognose getConfigSolarprognose()
  {
    return configSolarprognose;
  }

  public ConfigOpenWeather getConfigOpenWeather()
  {
    return configOpenWeather;
  }

  public ConfigAwattar getConfigAwattar()
  {
    return configAwattar;
  }

  public ConfigMessenger getConfigMessenger()
  {
    return configMessenger;
  }

  public List<ConfigDatabase> getConfigDatabases()
  {
    return configDatabases;
  }

  public List<ConfigMqtt> getConfigMqtts()
  {
    return configMqtts;
  }

  public ConfigDatabase getDatabaseFromUuid(String uuid)
  {
    for (ConfigDatabase configDatabase : configDatabases)
    {
      if (configDatabase.getUuid().equals(uuid))
      {
        return configDatabase;
      }
    }
    return new ConfigDatabase();
  }

  public ConfigDevice getDeviceFromUuid(String uuid)
  {
    for (ConfigDevice configDevice : configDevices)
    {
      if (configDevice.getUuid().equals(uuid))
      {
        return configDevice;
      }
    }
    return new ConfigDevice();
  }

  public ConfigMqtt getMqttFromUuid(String uuid)
  {
    for (ConfigMqtt configMqtt : configMqtts)
    {
      if (configMqtt.getUuid().equals(uuid))
      {
        return configMqtt;
      }
    }
    return new ConfigMqtt();
  }

}
