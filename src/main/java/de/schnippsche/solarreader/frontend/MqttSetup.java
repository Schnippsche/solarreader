package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.exporter.MqttExporter;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class MqttSetup
{
  private static ConfigMqtt currentMqtt;
  private final Map<String, String> formValues;
  private final DialogHelper dialogHelper;

  public MqttSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    this.dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action)
  {

    if ("checkmqtt".equals(action))
    {
      return check();
    }
    return null;
  }

  public String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
    if (step.isEmpty())
    {
      return "";
    }
    switch (step)
    {
      case "newmqtt":
        currentMqtt = new ConfigMqtt();
        return show();
      case "savemqtt":
        return save();
      case "editmqtt":
        currentMqtt = Config.getInstance().getMqttFromUuid(formValues.getOrDefault("id", "0"));
        return show();
      case "confirmdeletemqtt":
        return confirmDelete();
      case "deletemqtt":
        Config.getInstance().removeMqtt(currentMqtt);
        return "";
      default:
        return "";
    }
  }

  public String show()
  {
    Map<String, String> mqttMap = new HashMap<>();
    mqttMap.put("[mqtttitle]", currentMqtt.getDescription());
    mqttMap.put("[mqtthost]", currentMqtt.getHost());
    mqttMap.put("[mqttport]", "" + currentMqtt.getPort());
    mqttMap.put("[mqttname]", currentMqtt.getTopicName());
    mqttMap.put("[mqttuser]", currentMqtt.getUser());
    mqttMap.put("[mqttpassword]", currentMqtt.getPassword());
    mqttMap.put("[mqttenablechecked]", currentMqtt.isEnabled() ? "checked" : "");
    mqttMap.put("[mqttusesslchecked]", currentMqtt.isUseSsl() ? "checked" : "");
    mqttMap.put("[enabledelete]", currentMqtt.getDescription().isEmpty() ? "d-none" : "");
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "mqttmodal.tpl").getHtmlCode(mqttMap);
  }

  private String confirmDelete()
  {
    Map<String, String> infomap = new HashMap<>();
    infomap.put("[description]", currentMqtt.getDescription());
    infomap.put("[host]", currentMqtt.getHost());
    infomap.put("[port]", "" + currentMqtt.getPort());
    infomap.put("[topic]", currentMqtt.getTopicName());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "confirmdeletemqttmodal.tpl").getHtmlCode(infomap);
  }

  public String save()
  {
    if (!Config.getInstance().getConfigMqtts().contains(currentMqtt))
    {
      Config.getInstance().getConfigMqtts().add(currentMqtt);
    }
    dialogHelper.saveConfiguration();
    return "";
  }

  private AjaxResult check()
  {
    final String newName = formValues.getOrDefault("mqtttitle", "");
    long present = Config.getInstance()
                         .getConfigMqtts()
                         .stream()
                         .filter(cd -> cd.getDescription().equalsIgnoreCase(newName))
                         .filter(cd -> !cd.getUuid().equals(currentMqtt.getUuid()))
                         .count();
    if (present > 0)
    {
      return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{mqttsetup.name.exists}"));
    }
    currentMqtt.setDescription(newName);
    currentMqtt.setHost(formValues.getOrDefault("mqtthost", ""));
    currentMqtt.setPort(new NumericHelper().getInteger(formValues.getOrDefault("mqttport", "1883")));
    currentMqtt.setTopicName(formValues.getOrDefault("mqttname", ""));
    currentMqtt.setUser(formValues.getOrDefault("mqttuser", ""));
    currentMqtt.setPassword(formValues.getOrDefault("mqttpassword", ""));
    currentMqtt.setEnabled(formValues.getOrDefault("mqttenable", "off").equals("on"));
    currentMqtt.setUseSsl(formValues.getOrDefault("mqttusessl", "off").equals("on"));

    MqttExporter exporter = new MqttExporter(currentMqtt);
    Pair result = exporter.test();
    String text = SolarMain.languageHelper.replacePlaceholder(result.getValue());
    return new AjaxResult("ok".equals(result.getKey()), text);
  }

}
