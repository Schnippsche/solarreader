package de.schnippsche.solarreader.backend.automation.expressions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.CompareOperator;
import de.schnippsche.solarreader.backend.automation.ConditionalOperator;
import de.schnippsche.solarreader.backend.automation.Rule;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MqttTopicExpression extends Expression implements PropertyChangeListener
{
  private String brokerUuid;
  private String topic;
  private String payload;
  private CompareOperator compareOperator;
  private transient ExpiringCommand expiringCommand;
  private transient Rule rule;

  public MqttTopicExpression()
  {
    super(false);
    this.topic = "";
    this.payload = "";
    this.compareOperator = CompareOperator.EQUAL;
    this.brokerUuid = null;
  }

  public String getBrokerUuid()
  {
    return brokerUuid;
  }

  public String getTopic()
  {
    return topic;
  }

  public String getPayload()
  {
    return payload;
  }

  @Override public boolean isTrue()
  {
    return expiringCommand != null && expiringCommand.isValid() && expiringCommand.getUuid().equals(brokerUuid)
           && expiringCommand.getCommand().equals(topic)
           && compare(expiringCommand.getValue(), compareOperator, payload);

  }

  @Override public void initialize(Rule rule)
  {
    Config.getInstance().getMqttMaster().subscribeTopic(this);
    Config.getInstance().getMqttMaster().addPropertyChangeListener(brokerUuid, this);
    this.rule = rule;
  }

  @Override public void cleanup()
  {
    // dont unsubscribe but remove listener
    Config.getInstance().getMqttMaster().removePropertyChangeListener(brokerUuid, this);
  }

  @Override public void clear()
  {
    expiringCommand = null;
  }

  @Override public String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "conditionmqtttopicvalue.tpl");
    }
    infomap.put("[TOPIC]", topic);
    infomap.put("[PAYLOAD]", payload);
    infomap.put("[comparators]", Expression.getComparatorHtml(compareOperator));
    List<Pair> mqtts = new ArrayList<>();
    for (ConfigMqtt mqtt : Config.getInstance().getConfigMqtts())
    {
      mqtts.add(new Pair(mqtt.getUuid(), mqtt.getDescription()));
    }
    infomap.put("[mqttlist]", new HtmlOptionList(mqtts).getOptions(brokerUuid));
    String html = template.getHtmlCode(infomap);
    return SolarMain.languageHelper.replacePlaceholder(html);

  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    topic = newValues.getOrDefault("topic_" + getUuid(), "").trim();
    compareOperator =
      CompareOperator.valueOf(newValues.getOrDefault("comparator_" + getUuid(), CompareOperator.EQUAL.toString()));
    brokerUuid = newValues.getOrDefault("mqttbroker_" + getUuid(), "");
    setConditionalOperator(ConditionalOperator.valueOf(newValues.getOrDefault(
      "compare_" + getUuid(), ConditionalOperator.AND.toString())));
    payload = newValues.getOrDefault("payload_" + getUuid(), "").trim();
  }

  @Override public String toString()
  {
    return "MqttTopicExpression{" + "brokerUuid='" + brokerUuid + '\'' + ", topic='" + topic + '\'' + ", payload='"
           + payload + '\'' + ", operator=" + compareOperator + '}';
  }

  @Override public String getSummary()
  {
    String broker = Config.getInstance().getConfigMqttFromUuid(brokerUuid).getDescription();
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.mqttvalue.summary}");
    // Topic '%s' von Broker '%s' und Wert %s %s
    getTranslatedCompareOperator(compareOperator);
    return String.format(formatter, topic, broker, getTranslatedCompareOperator(compareOperator), payload);

  }

  @Override public void propertyChange(PropertyChangeEvent propertyChangeEvent)
  {
    ExpiringCommand newExpiringCommand = (ExpiringCommand) propertyChangeEvent.getNewValue();
    if (newExpiringCommand.getUuid().equals(brokerUuid) && newExpiringCommand.getCommand().equals(topic))
    {
      expiringCommand = newExpiringCommand;
      rule.doRule();
    }
  }

}
