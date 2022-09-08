package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlSidenav extends HtmlElement
{
  private final Map<String, String> values = new HashMap<>();

  public HtmlSidenav()
  {
    super(SolarMain.TEMPLATES_PATH + "sidenav.tpl");
  }

  @Override public String getHtmlCode()
  {
    StringBuilder builder = new StringBuilder();
    // create device list in navigation
    List<AbstractDevice> devices = Config.getInstance().getDevices();
    for (AbstractDevice device : devices)
    {
      builder.append(createLink(device.getConfigDevice().getDescription(), "editdevice", device.getConfigDevice()
                                                                                               .getUuid()));
    }
    values.put("[devicelist]", builder.toString());
    // create service list
    builder.setLength(0);
    builder.append(createLink("{sidenav.openweather}", "editopenweather", "0"));
    builder.append(createLink("{sidenav.solarprognose}", "editsolarprognose", "0"));
    builder.append(createLink("{sidenav.awattar}", "editawattar", "0"));
    values.put("[servicelist]", builder.toString());
    // create database list
    builder.setLength(0);
    for (ConfigDatabase configDatabase : Config.getInstance().getConfigDatabases())
    {
      builder.append(createLink(configDatabase.getDescription(), "editdatabase", configDatabase.getUuid()));
    }
    values.put("[databaselist]", builder.toString());
    // create mqtt list
    builder.setLength(0);
    for (ConfigMqtt configMqtt : Config.getInstance().getConfigMqtts())
    {
      builder.append(createLink(configMqtt.getDescription(), "editmqtt", configMqtt.getUuid()));
    }
    values.put("[mqttlist]", builder.toString());
    return super.getHtmlCode(values);
  }

  private String createLink(String text, String step, String id)
  {
    return String.format("<form method=\"post\"><input type=\"hidden\" name=\"step\" value=\"%s\"><input type=\"hidden\" name=\"id\" value=\"%s\"><button type=\"submit\" class=\"nav-link btn\"><div class=\"sb-nav-link-icon\"><em class=\"bi bi-sun\"></em></div>%s</button></form>", step, id, text);
  }

}
