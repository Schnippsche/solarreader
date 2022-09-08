package de.schnippsche.solarreader.backend.pusher;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MqttPusher extends AbstractPushBuffer<MqttPushValue>
{
  private final HashMap<String, IMqttClient> connectionMappings;
  private final List<IMqttClient> realConnections;

  public MqttPusher()
  {
    super(Config.getInstance().getConfigGeneral().getMqttPushSecondsTimeout());
    connectionMappings = new HashMap<>();
    realConnections = new ArrayList<>();
  }

  @Override protected void open()
  {
    // Get all possible mqtt connections and remove duplicate server
    connectionMappings.clear();
    realConnections.clear();
    Logger.debug("open mqtt connections");
    HashMap<String, IMqttClient> items = new HashMap<>();
    for (ConfigMqtt configMqtt : Config.getInstance().getConfigMqtts())
    {
      String url = configMqtt.getUrl();
      if (items.containsKey(url))
      {
        connectionMappings.put(configMqtt.getUuid(), items.get(url));
      } else
      {
        IMqttClient client = getConnection(configMqtt);
        if (client != null)
        {
          realConnections.add(client);
          connectionMappings.put(configMqtt.getUuid(), client);
          items.put(url, client);
        } else
        {
          Logger.debug("no connection available for {}", url);
        }
      }
    }
    Logger.debug("{} real connections for {} mqtt configs opened", realConnections.size(), connectionMappings.size());
  }

  @Override protected void close()
  {
    if (realConnections.isEmpty())
    {
      return;
    }
    Logger.debug("disconnect {} mqtt connections", realConnections.size());
    for (IMqttClient mqttClient : realConnections)
    {
      closeConnection(mqttClient);
    }
  }

  private IMqttClient getConnection(ConfigMqtt configMqtt)
  {
    try
    {
      String url = configMqtt.getUrl();
      Logger.debug("try to connect to mqtt url '{}'...", url);
      IMqttClient publisher = new MqttClient(url, configMqtt.getMainTopic(), new MemoryPersistence());
      MqttConnectOptions options = configMqtt.getMqttOptions();
      publisher.connect(options);
      Logger.info("connected to mqtt broker '{}'", publisher.getClientId());
      return publisher;
    } catch (MqttException e)
    {
      Logger.error("can't connect to mqtt url '{}' , reason: {}", configMqtt.getUrl(), e.getMessage());
    }
    return null;
  }

  private void closeConnection(IMqttClient mqttClient)
  {
    try
    {
      Logger.info("disconnect mqtt broker {}", mqttClient.getClientId());
      if (mqttClient.isConnected())
      {
        mqttClient.disconnect();
      }
    } catch (Exception e)
    {
      Logger.error("can't disconnect mqtt connection {}", e.getMessage());
    }
  }

  @Override public PushResult push(PushValue<MqttPushValue> pushValue)
  {
    Logger.debug("push mqtt message {}", pushValue);
    MqttPushValue mqttPushValue = pushValue.getSource();
    final IMqttClient connection = connectionMappings.get(mqttPushValue.getMqttuuid());
    if (connection == null)
    {
      Logger.error("connection for mqtt uuid {} is not established", mqttPushValue.getMqttuuid());
      return PushResult.RETRY;
    }
    // okay, try to push value
    try
    {
      if (connection.isConnected())
      {
        connection.publish(mqttPushValue.getTopic(), mqttPushValue.getMessage());
        return PushResult.SUCCESFUL;
      }
      Logger.error("not connected!");
    } catch (Exception e)
    {
      Logger.error("can't publish message {}", e.getMessage());
    }
    return PushResult.RETRY;
  }

}
