package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.automation.expressions.MqttTopicExpression;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.pusher.MqttPushValue;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.tinylog.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import static org.eclipse.paho.client.mqttv3.MqttClient.generateClientId;

public class MqttMasterClient implements MqttCallback
{
  private final ConfigMqtt configMqtt;
  private final PropertyChangeSupport support;
  private IMqttClient mqttClient;

  public MqttMasterClient(ConfigMqtt configMqtt)
  {
    this.configMqtt = configMqtt;
    this.support = new PropertyChangeSupport(this);
  }

  public boolean connect()
  {
    String url = configMqtt.getUrl();
    Logger.debug("try to connect to mqtt url '{}'...", url);
    try
    {
      mqttClient = new MqttClient(url, generateClientId(), new MemoryPersistence());
      MqttConnectOptions options = configMqtt.getMqttOptions();
      mqttClient.connect(options);
      mqttClient.setCallback(this);
      Logger.info("connected to mqtt broker with client id '{}'", mqttClient.getClientId());
      return true;

    } catch (Exception e)
    {
      Logger.error("can't connect to mqtt url '{}' , reason: {}", configMqtt.getUrl(), e.getMessage());
    }
    return false;
  }

  @Override public void connectionLost(Throwable cause)
  {
    try
    {
      mqttClient.reconnect();
    } catch (MqttException e)
    {
      Logger.error("can't reconnect to host {}", configMqtt.getHost());
    }
  }

  public void disconnect()
  {
    try
    {
      if (mqttClient.isConnected())
      {
        mqttClient.disconnect();
      }
    } catch (Exception e)
    {
      Logger.error("can't disconnect mqtt connection {}", e.getMessage());
    }
  }

  public void subscribe(MqttTopicExpression expression)
  {
    String topic = expression.getTopic().trim();
    Logger.debug("subscribe '{}' from host {}", topic, configMqtt.getHost());
    try
    {
      mqttClient.subscribe(topic);
    } catch (MqttException e)
    {
      Logger.error("can't subscribe topic '{}': {}", topic, e.getMessage());
    }
  }

  public void unsubscribe(MqttTopicExpression expression)
  {
    String topic = expression.getTopic().trim();
    Logger.debug("unsubscripe '{}' from host {}", topic, configMqtt.getHost());
    try
    {
      mqttClient.unsubscribe(topic);
    } catch (MqttException e)
    {
      Logger.error("can't unsubscribe topic '{}':{}", topic, e.getMessage());
    }
  }

  public boolean publishMessage(String topic, MqttMessage mqttMessage)
  {
    try
    {
      if (!mqttClient.isConnected())
      {
        Logger.debug("reconnect!");
        mqttClient.reconnect();
      }
      Logger.debug("publish topic '{}' with payload '{}'", topic.trim(), mqttMessage.toString());
      mqttClient.publish(topic.trim(), mqttMessage);
      return true;
    } catch (Exception e)
    {
      Logger.error("couldn't publish message, reason: {}", e.getMessage());
    }
    return false;
  }

  public boolean publishMessage(MqttPushValue pushValue)
  {
    return publishMessage(pushValue.getTopic(), pushValue.getMessage());
  }

  @Override public void messageArrived(String topic, MqttMessage message) throws Exception
  {
    String payload = new String(message.getPayload());
    Logger.info("message arrived, topic={}, message={}", topic, payload);
    ExpiringCommand command = new ExpiringCommand(configMqtt.getUuid(), topic, payload, 1);
    support.firePropertyChange(topic, null, command);
  }

  @Override public void deliveryComplete(IMqttDeliveryToken token)
  {

  }

  public void addPropertyChangeListener(PropertyChangeListener pcl)
  {
    support.addPropertyChangeListener(pcl);
  }

  public void removePropertyChangeListener(PropertyChangeListener pcl)
  {
    support.removePropertyChangeListener(pcl);
  }

}
