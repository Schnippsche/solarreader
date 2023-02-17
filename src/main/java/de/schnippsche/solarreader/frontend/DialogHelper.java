package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.Command;
import de.schnippsche.solarreader.backend.automation.CommandType;
import de.schnippsche.solarreader.backend.configuration.*;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlCheckboxList;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.tinylog.Logger;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    map.put("[mqtttopic]", configExport.getMqttTopic());
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
    for (Map.Entry<String, String> entry : formValues.entrySet())
    {
      if (entry.getKey().startsWith("databases"))
      {
        configExport.addDatabase(Config.getInstance().getConfigDatabaseFromUuid(entry.getValue()));
      } else if (entry.getKey().startsWith("mqtts"))
      {
        configExport.addMqtt(Config.getInstance().getConfigMqttFromUuid(entry.getValue()));
      }
    }

    configExport.setMqttTopic(formValues.getOrDefault("mqtttopic", ""));
    return configExport;
  }

  public List<Pair> getCachedResultFieldsFor(String deviceUuid)
  {
    List<Pair> deviceResultFields = new ArrayList<>();
    List<ResultField> fields = Config.getInstance().getCurrentResultFieldsForUuid(deviceUuid);
    if (fields.isEmpty())
    {
      deviceResultFields.add(new Pair("", SolarMain.languageHelper.replacePlaceholder("{rulesetup.dialoghelper.novalues}")));
    } else
    {
      deviceResultFields = fields.stream().sorted(Comparator.comparing(ResultField::getName))
                                 .map(f -> new Pair(f.getName(), f.getName() + " [" + f.getValue() + "]"))
                                 .collect(Collectors.toList());
    }
    return deviceResultFields;
  }

  public Command getFirstCommand()
  {
    for (ConfigDevice configDevice : Config.getInstance().getConfigDevices())
    {
      if (configDevice.getAutomationCommands() != null && !configDevice.getAutomationCommands().isEmpty())
      {
        for (Command command : configDevice.getAutomationCommands())
        {
          return command;
        }
      }
    }
    return null;

  }

  public List<Pair> getTypesFor(String deviceUuid)
  {
    List<Pair> list = new ArrayList<>();
    Set<Pair> uniqueValues = new HashSet<>();
    for (ConfigDevice configDevice : Config.getInstance().getConfigDevices())
    {
      if (configDevice.getUuid().equals(deviceUuid) && configDevice.getAutomationCommands() != null
          && !configDevice.getAutomationCommands().isEmpty())
      {
        for (Command command : configDevice.getAutomationCommands())
        {
          Pair pair = new Pair(
            command.getCommandType() + "@" + command.getIndex(), command.getCommandType() + " " + command.getIndex());
          if (uniqueValues.add(pair))
          {
            list.add(pair);
          }
        }
      }
    }
    return list;
  }

  public List<Pair> getActionsFor(String deviceUuid, CommandType commandType, int index)
  {
    List<Pair> list = new ArrayList<>();
    for (ConfigDevice configDevice : Config.getInstance().getConfigDevices())
    {
      if (configDevice.getUuid().equals(deviceUuid) && configDevice.getAutomationCommands() != null
          && !configDevice.getAutomationCommands().isEmpty())
      {
        for (Command command : configDevice.getAutomationCommands())
        {
          if (command.getCommandType().equals(commandType) && command.getIndex() == index)
          {
            Pair pair = new Pair(command.getCommandAction().toString(), command.getCommandAction().toString());
            list.add(pair);
          }
        }
      }
    }
    return list;
  }

  public synchronized void saveConfiguration()
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
