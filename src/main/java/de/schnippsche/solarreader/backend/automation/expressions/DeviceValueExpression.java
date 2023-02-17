package de.schnippsche.solarreader.backend.automation.expressions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.CompareOperator;
import de.schnippsche.solarreader.backend.automation.ConditionalOperator;
import de.schnippsche.solarreader.backend.automation.Rule;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.backend.worker.AwattarWorker;
import de.schnippsche.solarreader.backend.worker.OpenWeatherWorker;
import de.schnippsche.solarreader.backend.worker.SolarprognoseWorker;
import de.schnippsche.solarreader.frontend.DialogHelper;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeviceValueExpression extends Expression implements PropertyChangeListener
{
  private String deviceUuid;
  private String deviceName;
  private String deviceValue;
  private CompareOperator compareOperator;
  private transient ExpiringCommand expiringCommand;
  private transient Rule rule;

  public DeviceValueExpression()
  {
    super(false);
    this.compareOperator = CompareOperator.EQUAL;
  }

  @Override public boolean isTrue()
  {
    return expiringCommand != null && expiringCommand.isValid() && expiringCommand.getUuid().equals(deviceUuid)
           && expiringCommand.getCommand().equals(deviceName)
           && compare(expiringCommand.getValue(), compareOperator, deviceValue);
  }

  @Override public void initialize(Rule rule)
  {
    AbstractDevice device = Config.getInstance().getDeviceFromUuid(deviceUuid);
    if (device != null)
    {
      device.addPropertyChangeListener(this);
    }
    this.rule = rule;
  }

  @Override public void cleanup()
  {
    AbstractDevice device = Config.getInstance().getDeviceFromUuid(deviceUuid);
    if (device != null)
    {
      device.removePropertyChangeListener(this);
    }
  }

  @Override public void clear()
  {
    expiringCommand = null;
  }

  @Override public String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "conditiondevicevalue.tpl");
    }
    infomap.put("[DEVICEVALUE]", deviceValue);
    infomap.put("[comparators]", Expression.getComparatorHtml(compareOperator));
    infomap.put("[DEVICENAME]", deviceName);
    List<Pair> deviceList = new ArrayList<>();
    deviceList.add(new Pair("0", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.devicevalue.choose}")));
    for (AbstractDevice device : Config.getInstance().getDevices())
    {
      deviceList.add(new Pair(device.getConfigDevice().getUuid(), device.getConfigDevice().getDescription()));
    }
    deviceList.add(new Pair(OpenWeatherWorker.OPENWEATHER, OpenWeatherWorker.OPENWEATHER));
    deviceList.add(new Pair(SolarprognoseWorker.SOLARPROGNOSE, SolarprognoseWorker.SOLARPROGNOSE));
    deviceList.add(new Pair(AwattarWorker.AWATTAR, AwattarWorker.AWATTAR));
    infomap.put("[devicelist]", new HtmlOptionList(deviceList).getOptions(deviceUuid == null ? "0" : deviceUuid));
    // Search for latest result fields and get the values in select box
    List<Pair> pairs = new DialogHelper().getCachedResultFieldsFor(deviceUuid);
    infomap.put("[devicenames]", new HtmlOptionList(pairs).getOptions(deviceName));

    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));
  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    deviceName = newValues.getOrDefault("devicename_" + getUuid(), "");
    compareOperator =
      CompareOperator.valueOf(newValues.getOrDefault("comparator_" + getUuid(), CompareOperator.EQUAL.toString()));
    deviceValue = newValues.getOrDefault("devicevalue_" + getUuid(), "");
    deviceUuid = newValues.getOrDefault("device_" + getUuid(), "");
    setConditionalOperator(ConditionalOperator.valueOf(newValues.getOrDefault(
      "compare_" + getUuid(), ConditionalOperator.AND.toString())));
  }

  @Override public String getSummary()
  {
    String device;
    switch (deviceUuid)
    {
      case AwattarWorker.AWATTAR:
      case SolarprognoseWorker.SOLARPROGNOSE:
      case OpenWeatherWorker.OPENWEATHER:
        device = deviceUuid;
        break;
      default:
        device = Config.getInstance().getConfigDeviceFromUuid(deviceUuid).getDescription();
    }
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.devicevalue.summary}");
    return String.format(formatter, deviceName, device, getTranslatedCompareOperator(compareOperator), deviceValue);
  }

  public String getDeviceUuid()
  {
    return deviceUuid;
  }

  public String getDeviceName()
  {
    return deviceName;
  }

  public String getDeviceValue()
  {
    return deviceValue;
  }

  @Override public void propertyChange(PropertyChangeEvent propertyChangeEvent)
  {
    ExpiringCommand newExpiringCommand = (ExpiringCommand) propertyChangeEvent.getNewValue();
    if (newExpiringCommand.getUuid().equals(deviceUuid) && newExpiringCommand.getCommand().equals(deviceName))
    {
      expiringCommand = newExpiringCommand;
      rule.doRule();
    }
  }

}
