package de.schnippsche.solarreader.backend.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigExport
{
  private final Set<String> databaseList;
  private final Set<String> mqttList;

  public ConfigExport()
  {
    databaseList = new HashSet<>();
    mqttList = new HashSet<>();
  }

  public List<ConfigDatabase> getDatabaseList()
  {

    return databaseList.stream()
                       .map(uuid -> Config.getInstance().getDatabaseFromUuid(uuid))
                       .filter(ConfigDatabase::isEnabled)
                       .collect(Collectors.toList());
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
    return mqttList.stream()
                   .map(uuid -> Config.getInstance().getMqttFromUuid(uuid))
                   .filter(ConfigMqtt::isEnabled)
                   .collect(Collectors.toList());
  }

  public void addMqtt(ConfigMqtt configMqtt)
  {
    addMqtt(configMqtt.getUuid());
  }

  public void addMqtt(String configMqttUuid)
  {
    mqttList.add(configMqttUuid);
  }

}
