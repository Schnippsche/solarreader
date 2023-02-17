package de.schnippsche.solarreader.backend.automation.actions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.Command;
import de.schnippsche.solarreader.backend.automation.CommandAction;
import de.schnippsche.solarreader.backend.automation.CommandType;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.DialogHelper;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelaisAction extends Action
{
  private final transient DialogHelper dialogHelper;
  private final transient NumericHelper numericHelper;
  private transient String deviceUuid;
  private transient CommandType commandType;
  private transient CommandAction commandAction;
  private transient int index;
  private Command command;
  private transient String seconds;

  public RelaisAction()
  {
    super();
    dialogHelper = new DialogHelper();
    command = null;
    seconds = null;
    numericHelper = new NumericHelper();
  }

  public Command getCommand()
  {
    return command;
  }

  public void setCommand(Command command)
  {
    this.command = command;
  }

  @Override public void doAction()
  {
    final AbstractDevice device = Config.getInstance().getDeviceFromUuid(command.getDeviceUuid());
    if (device == null)
    {
      return;
    }
    final NetworkConnection networkConnection = new NetworkConnection();
    String user = device.getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_USER, "");
    String pass = device.getConfigDevice().getParamOrDefault(ConfigDeviceField.OPTIONAL_PASSWORD, "");
    networkConnection.setAuthorization(user, pass);
    networkConnection.testUrl(device.getAutomationCommandUrl(command));
  }

  @Override protected String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "actionsendrelais.tpl");
    }

    List<Pair> deviceList = new ArrayList<>();
    for (ConfigDevice configDevice : Config.getInstance().getConfigDevices())
    {
      if (configDevice.getAutomationCommands() != null && !configDevice.getAutomationCommands().isEmpty())
      {
        Pair pair = new Pair(configDevice.getUuid(), configDevice.getDescription());
        deviceList.add(pair);
      }
    }
    if (command == null)
    {
      // get first command
      command = dialogHelper.getFirstCommand();
    }
    if (command == null)
    {
      // no device with relais!
      deviceUuid = "";
      commandType = CommandType.RELAY;
      commandAction = CommandAction.OFF;
      index = 0;
      seconds = null;
    } else
    {
      deviceUuid = command.getDeviceUuid();
      commandType = command.getCommandType(); // RELAIS, LIGHT etc
      commandAction = command.getCommandAction(); // ON, OFF etc
      index = command.getIndex();
      seconds = command.getSeconds() != null ? "" + command.getSeconds() : null;
    }
    List<Pair> typeList = dialogHelper.getTypesFor(deviceUuid);
    List<Pair> actionList = dialogHelper.getActionsFor(deviceUuid, commandType, index);

    infomap.put("[devices]", new HtmlOptionList(deviceList).getOptions(deviceUuid));
    infomap.put("[commands]", new HtmlOptionList(typeList).getOptions(commandType.toString()));
    infomap.put("[choices]", new HtmlOptionList(actionList).getOptions(commandAction.toString()));
    infomap.put("[seconds]", seconds == null ? "" : seconds);
    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));
  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    deviceUuid = newValues.getOrDefault("device_" + getUuid(), "");
    String art = newValues.getOrDefault("command_" + getUuid(), "");
    String choice = newValues.getOrDefault("choice_" + getUuid(), "");
    seconds = newValues.getOrDefault("seconds_" + getUuid(), "");
    String[] arr = art.split("@");
    if (arr.length == 2)
    {
      try
      {
        commandType = CommandType.valueOf(arr[0]);
      } catch (Exception e)
      {
        commandType = CommandType.RELAY;
      }
      index = numericHelper.getInteger(arr[1], 0);
    }

    try
    {
      commandAction = CommandAction.valueOf(choice);
    } catch (Exception e)
    {
      commandAction = CommandAction.OFF;
    }
    if (seconds.isEmpty())
    {
      command = new Command(deviceUuid, commandType, index, commandAction);
    } else
    {
      command = new Command(deviceUuid, commandType, index, commandAction, numericHelper.getInteger(seconds, 0));
    }
  }

  @Override public String getSummary()
  {
    String device = Config.getInstance().getConfigDeviceFromUuid(deviceUuid).getDescription();
    // Sende Schaltbefehl '%s' an %s des Gerätes %s
    // Sende Schaltbefehl '%s' an %s des Gerätes %s Dauer Sekunden %
    String formatter;
    if (seconds != null && numericHelper.isNumericValue(seconds))
    {
      formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.relais.summarywithseconds}");
      return String.format(formatter, commandAction, commandType + " " + index, device, seconds);
    }
    formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.relais.summary}");
    return String.format(formatter, commandAction, commandType + " " + index, device);
  }

}
