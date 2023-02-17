package de.schnippsche.solarreader.backend.exporter;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.pusher.MqttPushValue;
import de.schnippsche.solarreader.backend.utils.MathEvalBigDecimal;
import de.schnippsche.solarreader.backend.utils.Pair;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.tinylog.Logger;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MqttExporter implements Exporter
{

  private static final String PUBLISHER_ID = "SolarreaderPublisher";
  private final ConfigMqtt configMqtt;
  private final List<ResultField> resultFields;
  private final String url;
  private final List<MqttField> mqttFields;
  private final MathEvalBigDecimal mathEval;
  private final MemoryPersistence persistence;
  private String mainTopic;

  public MqttExporter(ConfigMqtt configMqtt)
  {
    this(configMqtt, Collections.emptyList(), Collections.emptyList(), "");
  }

  public MqttExporter(ConfigMqtt configMqtt, List<ResultField> resultFields, List<MqttField> mqttFields, String topic)
  {
    this.resultFields = new ArrayList<>(resultFields);
    persistence = new MemoryPersistence();
    this.configMqtt = configMqtt;
    this.url =
      String.format("%s://%s:%s", configMqtt.isUseSsl() ? "ssl" : "tcp", configMqtt.getHost(), configMqtt.getPort());
    mainTopic = configMqtt.getMainTopic() + topic;
    if (!mainTopic.endsWith("/"))
    {
      mainTopic += "/";
    }
    this.mqttFields = new ArrayList<>(mqttFields);
    this.mathEval = new MathEvalBigDecimal();
  }

  public Pair test()
  {
    try (IMqttClient publisher = new MqttClient(url, PUBLISHER_ID, persistence))
    {
      MqttConnectOptions options = configMqtt.getMqttOptions();
      publisher.connect(options);
      publisher.disconnect();
    } catch (Exception ex)
    {
      Logger.error("can't connect or send to MQTT {}: {} ", url, ex);
      return new Pair("error", ex.getMessage());
    }

    return new Pair("ok", "");
  }

  @Override public synchronized void export()
  {
    Logger.debug("export to Mqtt {}", url);
    configMqtt.setLastCall(LocalDateTime.now());
    if (!fillValues())
    {
      return;
    }
    // New
    List<MqttPushValue> pushValues = new ArrayList<>();
    for (MqttField mqttField : mqttFields)
    {
      if (mqttField.getValue() != null)
      {
        MqttMessage message = createMessage(mqttField);
        String topic = mainTopic + mqttField.getName();
        MqttPushValue mqttPushValue = new MqttPushValue(configMqtt.getUuid(), topic, message);
        pushValues.add(mqttPushValue);
      }
    }
    Config.getInstance().getMqttMaster().publishMessage(pushValues);
  }

  private MqttMessage createMessage(MqttField mqttField)
  {
    MqttMessage msg = new MqttMessage();
    msg.setQos(0);
    msg.setRetained(false);
    msg.setPayload(String.valueOf(mqttField.getValue()).getBytes(StandardCharsets.UTF_8));
    return msg;
  }

  private boolean fillValues()
  {
    if (mqttFields == null || mqttFields.isEmpty())
    {
      Logger.info("mqtt fields empty; skip converting...");
      return false;
    }
    mathEval.setResultFieldsAsVariables(resultFields);
    boolean result = false;
    for (MqttField mqttField : mqttFields)
    {
      Object value = mathEval.calculateValue(mqttField.getSourcetype(), resultFields, mqttField.getSourcevalue());
      if (value != null)
      {
        mqttField.setValue(value);
        result = true;
      } else
      {
        Logger.debug("mqtt field '{}' with type '{}' is null or invalid!", mqttField.getSourcevalue(), mqttField.getSourcetype());
        mqttField.setValue(null);
      }
    }
    return result;
  }

}
