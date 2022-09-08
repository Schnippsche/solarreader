package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.configuration.ConfigExport;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlCheckboxList;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.tinylog.Logger;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DialogHelper
{
  private final NumericHelper numericHelper;

  public DialogHelper()
  {
    this.numericHelper = new NumericHelper();
  }

  public void setActivityValues(Map<String, String> map, Activity activity)
  {
    map.put("[ENABLED]", activity.isEnabled() ? "checked" : "");
    map.put("[ACTIVITYFROM]", activity.getStartTime().toString());
    map.put("[ACTIVITYTO]", activity.getEndTime().toString());
    map.put("[ACTIVITYINTERVAL]", String.valueOf(activity.getInterval()));
    List<Pair> timeUnits = new ArrayList<>();
    timeUnits.add(new Pair("SECONDS", "{activity.unit.seconds}"));
    timeUnits.add(new Pair("MINUTES", "{activity.unit.minutes}"));
    timeUnits.add(new Pair("HOURS", "{activity.unit.hours}"));
    map.put("[TIMEUNITS]", new HtmlOptionList(timeUnits).getOptions(activity.getTimeUnit().toString()));
  }

  public Activity getActivityFromForm(Map<String, String> formValues)
  {
    Activity activity = new Activity();
    activity.setEnabled("on".equals(formValues.getOrDefault("enable", "off")));
    activity.setStartTime(LocalTime.parse(formValues.get("activityfrom")));
    activity.setEndTime(LocalTime.parse(formValues.get("activityto")));
    activity.setInterval(numericHelper.getInteger(formValues.getOrDefault("activityinterval", "10")));
    activity.setTimeUnit(TimeUnit.valueOf(formValues.get("timeunit")));
    return activity;
  }

  /**
   * set the data exporter values into the modal dialog
   *
   * @param map          Map for inserting the values
   * @param configExport ConfigExport Class
   */
  public void setDataExporter(Map<String, String> map, ConfigExport configExport)
  {
    List<String> databaseActive = new ArrayList<>();
    for (ConfigDatabase database : configExport.getDatabaseList())
    {
      databaseActive.add(database.getUuid());
    }
    List<Pair> databases = new ArrayList<>();
    for (ConfigDatabase database : Config.getInstance().getConfigDatabases())
    {
      databases.add(new Pair(database.getUuid(), database.getDescription()));
    }
    map.put("[databasecheckboxes]", new HtmlCheckboxList().getHtml(databases, "databases", databaseActive));
    // Mqtt server
    List<String> mqttActive = new ArrayList<>();
    for (ConfigMqtt mqtt : configExport.getMqttList())
    {
      mqttActive.add(mqtt.getUuid());
    }
    List<Pair> mqtts = new ArrayList<>();
    for (ConfigMqtt mqtt : Config.getInstance().getConfigMqtts())
    {
      mqtts.add(new Pair(mqtt.getUuid(), mqtt.getDescription()));
    }
    map.put("[mqttcheckboxes]", new HtmlCheckboxList().getHtml(mqtts, "mqtts", mqttActive));
  }
  /**
   * get the export configuration from the submitted form values
   *
   * @param formValues Map with form values
   * @return ConfigExport
   */
  public ConfigExport getDataExporterFromForm(Map<String, String> formValues)
  {
    ConfigExport configExport = new ConfigExport();
    formValues.entrySet()
              .stream()
              .filter(entry -> entry.getKey().startsWith("databases"))
              .forEach(entry -> configExport.addDatabase(Config.getInstance().getDatabaseFromUuid(entry.getValue())));

    formValues.entrySet()
              .stream()
              .filter(entry -> entry.getKey().startsWith("mqtts"))
              .forEach(entry -> configExport.addMqtt(Config.getInstance().getMqttFromUuid(entry.getValue())));
    return configExport;
  }

  public void saveConfiguration()
  {
    try
    {
      Config.getInstance().writeConfiguration();
    } catch (Exception e)
    {
      Logger.error(e);
    }
  }

}
