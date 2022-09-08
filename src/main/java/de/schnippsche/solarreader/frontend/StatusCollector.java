package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.*;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatusCollector
{
  private static final String SUCCESS_CLASS = "bg-success";
  private static final String WARNING_CLASS = "bg-warning";
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
  private final String enabledText;
  private final String disabledText;
  private final List<StatusRow> rows;

  public StatusCollector()
  {
    rows = new ArrayList<>();
    enabledText = SolarMain.languageHelper.replacePlaceholder("{statusrow.enabled.text}");
    disabledText = SolarMain.languageHelper.replacePlaceholder("{statusrow.disabled.text}");
  }

  public String getAjax()
  {
    rows.clear();
    StatusRow row;
    // Devices
    for (AbstractDevice device : Config.getInstance().getDevices())
    {
      row = new StatusRow();
      row.setElement(device.getConfigDevice().getDescription());
      row.setIcon("img/inverter16.png");
      setActivity(device.getActivity().getLastCall(), row);
      row.setInfo("");
      setEnabledStatus(device.getActivity().isEnabled(), row);
      rows.add(row);
    }
    // Databases
    for (ConfigDatabase database : Config.getInstance().getConfigDatabases())
    {
      row = new StatusRow();
      row.setElement(database.getDescription());
      row.setIcon("img/influxdata16.png");
      setActivity(database.getLastCall(), row);
      row.setInfo("");
      setEnabledStatus(database.isEnabled(), row);
      rows.add(row);
    }
    // Mqtts
    for (ConfigMqtt mqtt : Config.getInstance().getConfigMqtts())
    {
      row = new StatusRow();
      row.setElement(mqtt.getDescription());
      row.setIcon("img/mqtt16.png");
      setActivity(mqtt.getLastCall(), row);
      row.setInfo("");
      setEnabledStatus(mqtt.isEnabled(), row);
      rows.add(row);
    }
    //
    ConfigAwattar awattar = Config.getInstance().getConfigAwattar();
    row = new StatusRow();
    row.setElement("aWATTar");
    row.setIcon("img/awattar16.png");
    setActivity(awattar.getActivity().getLastCall(), row);
    row.setInfo("");
    setEnabledStatus(awattar.getActivity().isEnabled(), row);
    rows.add(row);
    //
    ConfigOpenWeather openWeather = Config.getInstance().getConfigOpenWeather();
    row = new StatusRow();
    row.setElement("OpenWeather");
    row.setIcon("img/openweather16.png");
    setActivity(openWeather.getActivity().getLastCall(), row);
    row.setInfo("");
    setEnabledStatus(openWeather.getActivity().isEnabled(), row);
    rows.add(row);
    //
    ConfigSolarprognose solarprognose = Config.getInstance().getConfigSolarprognose();
    row = new StatusRow();
    row.setElement("Solarprognose");
    row.setIcon("img/solarprognose16.png");
    setActivity(solarprognose.getActivity().getLastCall(), row);
    row.setInfo("");
    setEnabledStatus(solarprognose.getActivity().isEnabled(), row);
    rows.add(row);

    return Config.getInstance().getGson().toJson(rows);
  }

  private void setActivity(LocalDateTime ldt, StatusRow row)
  {
    row.setActivity(ldt == null ? "" : ldt.format(formatter));
  }

  private void setEnabledStatus(boolean status, StatusRow row)
  {
    if (status)
    {
      row.setStatusclass(SUCCESS_CLASS);
      row.setStatustext(enabledText);
    } else
    {
      row.setStatusclass(WARNING_CLASS);
      row.setStatustext(disabledText);
    }
  }

}
