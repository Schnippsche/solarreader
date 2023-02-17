package de.schnippsche.solarreader.backend.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.schnippsche.solarreader.backend.automation.Rule;
import de.schnippsche.solarreader.backend.automation.RuleList;
import de.schnippsche.solarreader.backend.automation.actions.*;
import de.schnippsche.solarreader.backend.automation.expressions.*;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.gson.LocalDateSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.LocalDateTimeSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.LocalTimeSerializer;
import de.schnippsche.solarreader.backend.serializes.gson.TimeUnitSerializer;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import de.schnippsche.solarreader.backend.utils.JsonTools;
import de.schnippsche.solarreader.backend.utils.MqttMaster;
import org.tinylog.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Config
{
  public static final String CONFIG_FILE = "solarreaderconfig.json";
  private static final Config instance = new Config();
  private final transient StandardValues standardValues;
  private final transient Hashtable<String, Object> lockObjects; // Synchronized
  private final transient Hashtable<String, List<ResultField>> cachedResultFields; // Synchronized
  private final transient Deque<ExpiringCommand> expiringCommands;
  private final transient AppInfo appInfo;
  private final transient MqttMaster mqttMaster;
  private final transient RuleList ruleList;
  private transient List<AbstractDevice> devices;
  private List<ConfigDatabase> configDatabases;
  private List<ConfigMqtt> configMqtts;
  private ConfigGeneral configGeneral;
  private List<ConfigDevice> configDevices;
  private List<ConfigRule> configRules;

  // Solarprognose
  private ConfigSolarprognose configSolarprognose;
  // OpenWeather
  private ConfigOpenWeather configOpenWeather;
  // Awattar
  private ConfigAwattar configAwattar;
  // Messenger
  private ConfigMessenger configMessenger;
  private transient Gson gson;

  private Config()
  {
    configDevices = Collections.synchronizedList(new ArrayList<>());
    configDatabases = new ArrayList<>();
    configMqtts = new ArrayList<>();
    configOpenWeather = new ConfigOpenWeather();
    configSolarprognose = new ConfigSolarprognose();
    configGeneral = new ConfigGeneral();
    configAwattar = new ConfigAwattar();
    configMessenger = new ConfigMessenger();
    standardValues = new StandardValues();
    lockObjects = new Hashtable<>();
    cachedResultFields = new Hashtable<>();
    expiringCommands = new LinkedList<>();  // Thread safe
    devices = Collections.synchronizedList(new ArrayList<>());
    gson = getGson();
    appInfo = readAppInfo();
    mqttMaster = new MqttMaster();
    configRules = new ArrayList<>();
    ruleList = new RuleList();
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
      configRules = tmpConfig.getConfigRules();
      initDevices();
    } catch (Exception e)
    {
      Logger.error(e.getMessage());
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
      RuntimeTypeAdapterFactory<Action> runtimeTypeAdapterFactory =
        RuntimeTypeAdapterFactory.of(Action.class).registerSubtype(DeviceAction.class)
                                 .registerSubtype(DatabaseAction.class).registerSubtype(MqttAction.class)
                                 .registerSubtype(RelaisAction.class).registerSubtype(UrlAction.class);
      builder.registerTypeAdapterFactory(runtimeTypeAdapterFactory);
      RuntimeTypeAdapterFactory<Expression> exprTypeAdapterFactory =
        RuntimeTypeAdapterFactory.of(Expression.class).registerSubtype(DeviceValueExpression.class)
                                 .registerSubtype(DayExpression.class).registerSubtype(MqttTopicExpression.class)
                                 .registerSubtype(NightExpression.class).registerSubtype(PeriodExpression.class);
      builder.registerTypeAdapterFactory(exprTypeAdapterFactory);
      gson = builder.create();
    }
    return gson;
  }

  public MqttMaster getMqttMaster()
  {
    return mqttMaster;
  }

  public synchronized List<AbstractDevice> getDevices()
  {
    return devices;
  }

  public synchronized void removeDevice(ConfigDevice device)
  {
    Logger.info("remove device {}", device.getDescription());
    devices.removeIf(abstractDevice -> abstractDevice.getConfigDevice().equals(device));
    configDevices.remove(device);
    saveConfiguration();
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
    mqttMaster.removeClient(mqtt);
    saveConfiguration();
  }

  public synchronized void removeRule(ConfigRule configRule)
  {
    configRule.setEnabled(false);
    Logger.info("remove rule {}", configRule.getTitle());
    ruleList.remove(configRule);
    configRules.remove(configRule);
    saveConfiguration();
  }

  private void updateLockObjects(ConfigDevice configDevice)
  {
    String lockObjectString = getLockObjectIdentifier(configDevice);
    if (!lockObjectString.isEmpty())
    {
      lockObjects.computeIfAbsent(lockObjectString, k -> new Object());
    }
  }

  private synchronized void initDevices()
  {
    Logger.debug("init devices...");
    devices = new ArrayList<>();
    for (ConfigDevice configDevice : configDevices)
    {
      AbstractDevice device = getDeviceFromClassName(configDevice);
      updateLockObjects(configDevice);
      if (device != null)
      {
        devices.add(device);
      }
    }
    Logger.debug("lock objects size={}, {}", lockObjects.size(), lockObjects);
  }

  public synchronized void initRules()
  {
    Logger.debug("init rules...");
    for (ConfigRule configRule : configRules)
    {
      Rule rule = new Rule(configRule);
      ruleList.addRule(rule);
      rule.initialize();
    }
  }

  public synchronized void addOrReplaceDevice(ConfigDevice updatedConfigDevice)
  {
    updateLockObjects(updatedConfigDevice);
    for (AbstractDevice device : devices)
    {
      if (device.getConfigDevice().getUuid().equals(updatedConfigDevice.getUuid()))
      {
        device.changeConfiguration(updatedConfigDevice);
        saveConfiguration();
        return;
      }
    }
    // Nothing found, add new class
    AbstractDevice device = getDeviceFromClassName(updatedConfigDevice);
    if (device != null)
    {
      devices.add(device);
      configDevices.add(updatedConfigDevice);
      saveConfiguration();
    }
  }

  public AbstractDevice getDeviceFromUuid(String uuid)
  {
    for (AbstractDevice device : devices)
    {
      if (device.getConfigDevice().getUuid().equals(uuid))
      {
        return device;
      }
    }
    return null;
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

  public AbstractDevice getDeviceFromClassName(ConfigDevice configDevice)
  {
    try
    {
      final Object clazz = Class.forName("de.schnippsche.solarreader.backend.devices." + configDevice.getDeviceClass())
                                .getConstructor(ConfigDevice.class).newInstance(configDevice);
      return (AbstractDevice) clazz;
    } catch (Exception e)
    {
      Logger.error(e.getMessage());
    }
    return null;
  }

  public RuleList getRuleList()
  {
    return this.ruleList;
  }

  public AppInfo getAppInfo()
  {
    return this.appInfo;
  }

  public StandardValues getStandardValues()
  {
    return standardValues;
  }

  public synchronized Object getLockObject(ConfigDevice configDevice)
  {
    String lockObject = getLockObjectIdentifier(configDevice);
    Logger.debug("get lock object for {} = {}", lockObject, lockObjects.getOrDefault(lockObject, new Object()));
    return lockObjects.getOrDefault(lockObject, new Object());
  }

  private String getLockObjectIdentifier(ConfigDevice configDevice)
  {

    if (configDevice.containsField(ConfigDeviceField.COM_PORT))
    {
      return configDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, "").trim().toUpperCase();
    }
    if (configDevice.containsField(ConfigDeviceField.HIDRAW_PATH))
    {
      return configDevice.getParamOrDefault(ConfigDeviceField.HIDRAW_PATH, "").trim().toUpperCase();
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

  private synchronized void saveConfiguration()
  {
    try
    {
      writeConfiguration();
    } catch (IOException e)
    {
      Logger.error(e.getMessage());
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

  public List<ConfigRule> getConfigRules()
  {
    return configRules;
  }

  public ConfigDatabase getConfigDatabaseFromUuid(String uuid)
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

  public ConfigDevice getConfigDeviceFromUuid(String uuid)
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

  public ConfigMqtt getConfigMqttFromUuid(String uuid)
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

  public ConfigRule getConfigRuleFromUuid(String uuid)
  {
    for (ConfigRule configRule : configRules)
    {
      if (configRule.getUuid().equals(uuid))
      {
        return configRule;
      }
    }
    return new ConfigRule();
  }

  public List<ResultField> getCurrentResultFieldsForUuid(String uuid)
  {
    List<ResultField> result = null;
    if (uuid != null)
    {
      result = cachedResultFields.get(uuid);
    }
    return (result != null ? result : Collections.emptyList());
  }

  public synchronized void setCurrentResultFields(String uuid, List<ResultField> resultFields)
  {
    Logger.debug("set cached resultfields for uuid {}:{}", uuid, resultFields);
    if (uuid != null && resultFields != null)
    {
      this.cachedResultFields.put(uuid, resultFields);
    }
  }

  /**
   * removes all commands which expires 5 minutes
   */
  public void removeExpiredCommands()
  {
    final Iterator<ExpiringCommand> each = expiringCommands.iterator();
    while (each.hasNext())
    {
      ExpiringCommand cmd = each.next();
      if (ChronoUnit.MINUTES.between(cmd.getCommandTime(), LocalDateTime.now()) > 5)
      {
        Logger.info("remove expired command {}", cmd);
        each.remove();
      }
    }
  }

  public List<ExpiringCommand> getExpiringCommandsForUuid(String uuid)
  {
    removeExpiredCommands();
    List<ExpiringCommand> list = new ArrayList<>();
    for (ExpiringCommand cmd : this.expiringCommands)
    {
      if (cmd.getUuid().equals(uuid))
      {
        list.add(cmd);
      }
    }
    return list;
  }

  public void addExpiringCommand(ExpiringCommand expiringCommand)
  {
    expiringCommands.add(expiringCommand);
  }

  public void removeExpiringCommand(ExpiringCommand expiringCommand)
  {
    expiringCommands.remove(expiringCommand);
  }

}
