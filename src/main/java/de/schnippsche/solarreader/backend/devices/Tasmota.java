package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.automation.Command;
import de.schnippsche.solarreader.backend.automation.CommandAction;
import de.schnippsche.solarreader.backend.automation.CommandType;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import okhttp3.Credentials;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tasmota extends AbstractDevice
{
  private String url;
  private String credentials;
  private String statusUrl;

  public Tasmota(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    String user = getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_USER, "");
    String pass = getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_PASSWORD, "");
    url =
      String.format("http://%s:%s", getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost"), getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80"));
    statusUrl = url + "/cm?cmnd=Status0";
    credentials = !user.isEmpty() && !pass.isEmpty() ? Credentials.basic(user, pass) : null;
  }

  @Override protected boolean readDeviceValues()
  {
    Logger.info("try to connect to " + url);
    resultFields.clear();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(statusUrl, credentials));
    return !resultFields.isEmpty();
  }

  @Override protected void correctValues()
  {
    setBooleanToNumber(getValidResultField("StatusSTS_POWER"));
    setBooleanToNumber(getValidResultField("StatusSTS_POWER1"));
    setBooleanToNumber(getValidResultField("StatusSTS_POWER2"));
  }

  @Override public boolean checkConnection()
  {
    initialize();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(statusUrl, credentials));
    ResultField field = getValidResultField("Status_DeviceName");
    if (field != null)
    {
      Logger.info("Tasmota device with name {} detected!", field.getValue());
    }
    return (field != null);
  }

  private void setBooleanToNumber(ResultField resultField)
  {
    if (resultField != null)
    {
      resultField.setValue("ON".equals(resultField.getValue()) ? BigDecimal.ONE : BigDecimal.ZERO);
    }
  }

  @Override protected List<Command> getAutomationCommands()
  {
    // check relays, rollers and lights
    Pattern pattern = Pattern.compile("StatusSTS_POWER(\\d*)");
    String deviceUuid = getConfigDevice().getUuid();
    List<Command> commands = new ArrayList<>();
    for (ResultField resultField : resultFields)
    {
      Matcher matcher = pattern.matcher(resultField.getName());
      if (matcher.find())
      {
        int index = numericHelper.getInteger(matcher.group(1), 0);
        if (index == 0)
        {
          index = 1;
        }
        commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.ON));
        commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.OFF));
        commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TOGGLE));
        commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TIMED_ON));
        commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TIMED_OFF));
        if (index == 2)
        {
          commands.add(new Command(deviceUuid, CommandType.RELAY, 0, CommandAction.ALL_OFF));
          commands.add(new Command(deviceUuid, CommandType.RELAY, 0, CommandAction.ALL_ON));
        }
      }
    }
    Collections.sort(commands);
    return commands;
  }

  @Override public String getAutomationCommandUrl(Command command)
  {
    //  http://<ip>/cm?cmnd=Power%20TOGGLE
    // Power0 = all, Power1 = first
    String action;
    if (command.getCommandAction().equals(CommandAction.ALL_ON))
    {
      action = "on";
    } else if (command.getCommandAction().equals(CommandAction.ALL_OFF))
    {
      action = "off";
    } else
    {
      action = command.getCommandAction().toString().toLowerCase();
    }
    if (command.getSeconds() != null && action.toLowerCase().startsWith("timed_"))
    {
      action = action.replace("timed_", "");
      // 1..111 = set PulseTime for Relay<x> in 0.1 second increments
      //112..64900 = set PulseTime for Relay<x>, offset by 100, in 1 second increments.
      // Add 100 to desired interval in seconds, e.g., PulseTime 113 = 13 seconds and PulseTime 460 = 6 minutes (i.e., 360 seconds)
      int sec;
      if (command.getSeconds() < 12)
      {
        sec = command.getSeconds() * 10;
      } else
      {
        sec = command.getSeconds() + 100;
      }
      return String.format("%s/cm?cmnd=Power%s", url, command.getIndex()) + "%20" + action + "&PulseTime%20" + sec;
    }
    return String.format("%s/cm?cmnd=Power%s", url, command.getIndex()) + "%20" + action;
  }

}
