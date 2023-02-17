package de.schnippsche.solarreader.backend.automation.actions;
import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.pusher.MqttPushValue;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MqttAction extends Action
{
  private String mqttUuid;
  private String topic;
  private String payload;

  @Override public void doAction()
  {
    MqttMessage msg = new MqttMessage();
    msg.setQos(0);
    msg.setRetained(false);
    msg.setPayload(payload.getBytes(StandardCharsets.UTF_8));
    MqttPushValue pushValue = new MqttPushValue(mqttUuid, topic, msg);
    Config.getInstance().getMqttMaster().publishMessage(pushValue);
  }

  @Override protected String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "actionsendbroker.tpl");
    }
    List<Pair> deviceList = new ArrayList<>();
    for (ConfigMqtt mqtt : Config.getInstance().getConfigMqtts())
    {
      deviceList.add(new Pair(mqtt.getUuid(), mqtt.getDescription()));
    }
    infomap.put("[mqttlist]", new HtmlOptionList(deviceList).getOptions(mqttUuid));
    infomap.put("[TOPIC]", topic);
    infomap.put("[PAYLOAD]", payload);
    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));

  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    mqttUuid = newValues.getOrDefault("mqttbroker_" + getUuid(), "");
    topic = newValues.getOrDefault("topicname_" + getUuid(), "");
    payload = newValues.getOrDefault("payload_" + getUuid(), "");
  }

  @Override public String getSummary()
  {
    String broker = Config.getInstance().getConfigMqttFromUuid(mqttUuid).getDescription();
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.mqtt.summary}");
    return String.format(formatter, topic, broker);
  }

}
