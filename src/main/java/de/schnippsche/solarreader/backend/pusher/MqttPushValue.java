package de.schnippsche.solarreader.backend.pusher;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPushValue
{
  private final String mqttuuid;
  private final String topic;
  private final MqttMessage message;

  public MqttPushValue(String mqttuuid, String topic, MqttMessage message)
  {
    this.mqttuuid = mqttuuid;
    this.topic = topic;
    this.message = message;
  }

  public String getMqttuuid()
  {
    return mqttuuid;
  }

  public String getTopic()
  {
    return topic;
  }

  public MqttMessage getMessage()
  {
    return message;
  }

}
