package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.automation.expressions.MqttTopicExpression;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.pusher.MqttPushValue;
import org.tinylog.Logger;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MqttMaster
{
  // mqttclients: uuid, mqttclient
  private final Map<String, MqttMasterClient> mqttClients;

  public MqttMaster()
  {
    this.mqttClients = new HashMap<>();
  }

  public void addClient(ConfigMqtt configMqtt)
  {
    MqttMasterClient client = new MqttMasterClient(configMqtt);
    mqttClients.put(configMqtt.getUuid(), client);
    client.connect();
  }

  public void removeClient(ConfigMqtt configMqtt)
  {
    String uuid = configMqtt.getUuid();
    MqttMasterClient client = mqttClients.get(configMqtt.getUuid());
    client.disconnect();
    mqttClients.remove(uuid);
  }

  public void subscribeTopic(MqttTopicExpression expression)
  {
    MqttMasterClient client = mqttClients.get(expression.getBrokerUuid());
    if (client == null)
    {
      Logger.error("unknown mqtt uuid '{}'", expression.getBrokerUuid());
      return;
    }
    client.subscribe(expression);
  }

  public void unsubscribeTopic(MqttTopicExpression expression)
  {
    MqttMasterClient client = mqttClients.get(expression.getBrokerUuid());
    if (client == null)
    {
      Logger.error("unknown mqtt uuid '{}'", expression.getBrokerUuid());
      return;
    }
    client.unsubscribe(expression);
  }

  public void addPropertyChangeListener(String mqttBrokerUuid, PropertyChangeListener propertyChangeListener)
  {
    MqttMasterClient client = mqttClients.get(mqttBrokerUuid);
    if (client != null)
    {
      client.addPropertyChangeListener(propertyChangeListener);
    }
  }

  public void removePropertyChangeListener(String mqttBrokerUuid, PropertyChangeListener propertyChangeListener)
  {
    MqttMasterClient client = mqttClients.get(mqttBrokerUuid);
    if (client != null)
    {
      client.removePropertyChangeListener(propertyChangeListener);
    }
  }

  public synchronized boolean publishMessage(MqttPushValue pushValue)
  {
    MqttMasterClient client = mqttClients.get(pushValue.getMqttuuid());
    if (client == null)
    {
      Logger.error("unknown mqtt uuid '{}'", pushValue.getMqttuuid());
      return false;
    }
    return client.publishMessage(pushValue);
  }

  public synchronized void publishMessage(List<MqttPushValue> pushValues)
  {
    if (pushValues == null || pushValues.isEmpty())
    {
      return;
    }
    String mqttUuid = pushValues.get(0).getMqttuuid();
    ConfigMqtt config = Config.getInstance().getConfigMqttFromUuid(mqttUuid);
    MqttMasterClient client = mqttClients.get(mqttUuid);
    Logger.info("publish {} values to mqtt broker {}", pushValues.size(), config.getDescription());
    pushValues.forEach(client::publishMessage);

  }

  public void connectAllMqttClients()
  {
    for (ConfigMqtt configMqtt : Config.getInstance().getConfigMqtts())
    {
      addClient(configMqtt);
    }
  }

  public void disconnectAllMqttClients()
  {
    for (MqttMasterClient client : mqttClients.values())
    {
      client.disconnect();
    }
  }

}
