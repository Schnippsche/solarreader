package de.schnippsche.solarreader.backend.configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigExport
{
  private final Set<String> databaseList;
  private final Set<String> mqttList;
  private String mqttTopic;

  public ConfigExport()
  {
    databaseList = new HashSet<>();
    mqttList = new HashSet<>();
    mqttTopic = "";
  }

  public ConfigExport(ConfigExport otherConfigExport)
  {
    databaseList = new HashSet<>(otherConfigExport.databaseList);
    mqttList = new HashSet<>(otherConfigExport.mqttList);
    mqttTopic = otherConfigExport.getMqttTopic();
  }

  public List<ConfigDatabase> getDatabaseList()
  {

    List<ConfigDatabase> list = new ArrayList<>();
    for (String uuid : databaseList)
    {
      ConfigDatabase configDatabaseFromUuid = Config.getInstance().getConfigDatabaseFromUuid(uuid);
      if (configDatabaseFromUuid.isEnabled())
      {
        list.add(configDatabaseFromUuid);
      }
    }
    return list;
  }

  public void addDatabase(ConfigDatabase configDatabase)
  {
    addDatabase(configDatabase.getUuid());
  }

  public void addDatabase(String configDatabaseUuid)
  {
    databaseList.add(configDatabaseUuid);
  }

  public List<ConfigMqtt> getMqttList()
  {
    List<ConfigMqtt> list = new ArrayList<>();
    for (String uuid : mqttList)
    {
      ConfigMqtt configMqttFromUuid = Config.getInstance().getConfigMqttFromUuid(uuid);
      if (configMqttFromUuid.isEnabled())
      {
        list.add(configMqttFromUuid);
      }
    }
    return list;
  }

  public void addMqtt(ConfigMqtt configMqtt)
  {
    addMqtt(configMqtt.getUuid());
  }

  public void addMqtt(String configMqttUuid)
  {
    mqttList.add(configMqttUuid);
  }

  public String getMqttTopic()
  {
    return mqttTopic;
  }

  public void setMqttTopic(String mqttTopic)
  {
    this.mqttTopic = mqttTopic;
  }

}
