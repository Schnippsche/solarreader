package de.schnippsche.solarreader.backend.automation.actions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceAction extends Action
{
  private String deviceUuid;
  private String command;

  @Override public void doAction()
  {
    ExpiringCommand expiringCommand = new ExpiringCommand(deviceUuid, command);
    Logger.debug("add expiring command '{}' for DeviceAction , uuid = {}", command, deviceUuid);
    Config.getInstance().addExpiringCommand(expiringCommand);
  }

  @Override protected String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "actionsenddevice.tpl");
    }
    List<Pair> deviceList = new ArrayList<>();
    for (AbstractDevice device : Config.getInstance().getDevices())
    {
      deviceList.add(new Pair(device.getConfigDevice().getUuid(), device.getConfigDevice().getDescription()));
    }
    infomap.put("[devicelist]", new HtmlOptionList(deviceList).getOptions(deviceUuid));
    infomap.put("[COMMAND]", command);
    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));
  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    deviceUuid = newValues.getOrDefault("devicename_" + getUuid(), "");
    command = newValues.getOrDefault("sendname_" + getUuid(), "");
  }

  @Override public String getSummary()
  {
    String device = Config.getInstance().getConfigDeviceFromUuid(deviceUuid).getDescription();
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.device.summary}");
    return String.format(formatter, command, device);
  }

}
