package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.tinylog.Logger;

public class SonOff extends AbstractDevice implements MqttCallback
{
  private final MemoryPersistence persistence;
  private String url;
  private String[] topics;

  public SonOff(ConfigDevice configDevice)
  {
    super(configDevice);
    persistence = new MemoryPersistence();
  }

  @Override protected void initialize()
  {
    String topic = getConfigDevice().getParamOrDefault(ConfigDeviceField.TOPIC, "sonoff");

    topics = new String[2];
    topics[0] = String.format("+/%s/#", topic);
    topics[1] = String.format("%s/#", topic);
    this.url = String.format("tcp://%s:%s", "localhost", "1883"); // TODO: localhost und Port aus config nehmen
  }

  @Override protected boolean readDeviceValues()
  {
    Logger.debug("try to connect to " + url);
    String publisherId = "SolarreaderClient";
    try (IMqttClient subscriber = new MqttClient(url, publisherId, persistence))
    {
      MqttConnectOptions options = new MqttConnectOptions();
      options.setAutomaticReconnect(true);
      options.setCleanSession(true);
      options.setConnectionTimeout(30);
      options.setKeepAliveInterval(60);
      subscriber.setCallback(this);
      subscriber.connect(options);
      subscriber.subscribe(topics);
      Thread.sleep(6000);
      subscriber.disconnect();
      Logger.debug("disconnect from mqtt broker ");
      return true;
    } catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
    } catch (Exception ex)
    {
      Logger.error("can't connect or send to MQTT {}: {} ", url, ex);
    }
    Logger.debug("disconnected from mqtt {}", url);
    return false;
  }

  @Override protected void correctValues()
  {
  }

  @Override protected void createTables()
  {
  }

  @Override public void connectionLost(Throwable cause)
  {
    Logger.error("Connection lost!");
  }

  @Override public void messageArrived(String topic, MqttMessage message)
  {
    Logger.debug("message Arrived, topic={}, message={}", topic, new String(message.getPayload()));
  }

  @Override public void deliveryComplete(IMqttDeliveryToken token)
  {
    Logger.debug("Delivery Complete, token={}", token);
  }

}
