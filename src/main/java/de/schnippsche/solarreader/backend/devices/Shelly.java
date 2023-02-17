package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.automation.Command;
import de.schnippsche.solarreader.backend.automation.CommandAction;
import de.schnippsche.solarreader.backend.automation.CommandType;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import okhttp3.Credentials;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shelly extends AbstractDevice
{
  protected final DayValueWrapper dayValueWrapper;
  private String url;
  private String statusUrl;
  private String shellyUrl;
  private String credentials;

  public Shelly(ConfigDevice configDevice)
  {
    super(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    String user = getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_USER, "");
    String pass = getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_PASSWORD, "");
    String ip = getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost");
    String port = getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80");
    credentials = !user.isEmpty() && !pass.isEmpty() ? Credentials.basic(user, pass) : null;
    url = String.format("http://%s:%s", ip, port);
    statusUrl = url + "/Status";
    shellyUrl = url + "/shelly";
  }

  @Override protected void correctValues()
  {
    BigDecimal wattTotal = BigDecimal.ZERO;
    for (int i = 0; i < 3; i++)
    {
      ResultField wirkleistung = getValidResultField("emeters_" + i + "_power");
      if (wirkleistung != null)
      {
        BigDecimal factor = dayValueWrapper.getFactor(getConfigDevice().getActivity());
        BigDecimal watt = dayValueWrapper.addValue(wirkleistung, factor);
        wattTotal = wattTotal.add(watt);
      }
    }
    setWattTotalToday(wattTotal);
    resultFields.add(new ResultField("WattstundenGesamtHeute", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotal));
  }

  @Override public boolean checkConnection()
  {
    initialize();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(statusUrl, credentials));
    if (resultFields.isEmpty())
    {
      return false;
    }
    resultFields.clear();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(shellyUrl, credentials));
    ResultField typeField = getValidResultField("type");
    ResultField genField = getValidResultField("gen");
    int generation = genField != null ? genField.getNumericValue().intValue() : 1;
    if (typeField != null)
    {
      Logger.info("device detected as shelly type {}, gen {}", typeField.getValue(), generation);
      return true;
    }
    return false;
  }

  @Override protected boolean readDeviceValues()
  {

    Logger.info("try to connect to " + shellyUrl);
    resultFields.clear();
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(shellyUrl, credentials));
    if (resultFields.isEmpty())
    {
      Logger.error("couldn't read Shelly info from url {}", statusUrl);
      return false;
    }
    Logger.info("try to connect to " + statusUrl);
    resultFields.addAll(jsonTool.getResultFieldsFromUrl(statusUrl, credentials));

    return true;
  }

  @Override protected List<Command> getAutomationCommands()
  {
    // check relays, rollers and lights
    Pattern pattern = Pattern.compile("(relays|lights|rollers)_(\\d+)_(ison|power)");
    String deviceUuid = getConfigDevice().getUuid();
    List<Command> commands = new ArrayList<>();
    for (ResultField resultField : resultFields)
    {
      Matcher matcher = pattern.matcher(resultField.getName());
      if (matcher.find())
      {
        String type = matcher.group(1);
        int index = numericHelper.getInteger(matcher.group(2), 0) + 1;
        switch (type)
        {
          case "relays":
            commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.ON));
            commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.OFF));
            commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TOGGLE));
            commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TIMED_ON));
            commands.add(new Command(deviceUuid, CommandType.RELAY, index, CommandAction.TIMED_OFF));
            break;
          case "lights":
            commands.add(new Command(deviceUuid, CommandType.LIGHT, index, CommandAction.ON));
            commands.add(new Command(deviceUuid, CommandType.LIGHT, index, CommandAction.OFF));
            commands.add(new Command(deviceUuid, CommandType.LIGHT, index, CommandAction.TOGGLE));
            commands.add(new Command(deviceUuid, CommandType.LIGHT, index, CommandAction.TIMED_ON));
            commands.add(new Command(deviceUuid, CommandType.LIGHT, index, CommandAction.TIMED_OFF));
            break;
          case "rollers":
            commands.add(new Command(deviceUuid, CommandType.ROLLER, index, CommandAction.OPEN));
            commands.add(new Command(deviceUuid, CommandType.ROLLER, index, CommandAction.CLOSE));
            break;
          default:
            //
        }
      }
    }
    Collections.sort(commands);
    return commands;
  }

  @Override public String getAutomationCommandUrl(Command command)
  {
    String cmd = command.getCommandType().toString().toLowerCase();
    String action = command.getCommandAction().toString().toLowerCase();
    //   actionUrl = http://192.168.178.224/relay/0?turn=on&timer=5
    if (command.getSeconds() != null && action.startsWith("timed_"))
    {
      action = action.replace("timed_", "");
      return String.format("%s/%s/%s?turn=%s&timer=%s", url, cmd, command.getIndex() - 1, action, command.getSeconds());
    }

    return String.format("%s/%s/%s?turn=%s", url, cmd, command.getIndex() - 1, action);

  }

}
