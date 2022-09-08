package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.backend.worker.ThreadHelper;

import java.util.HashMap;
import java.util.Map;

public class OpenWeatherSetup
{
  private final Map<String, String> formValues;
  private final ConfigOpenWeather configOpenWeather;
  private final DialogHelper dialogHelper;

  public OpenWeatherSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    configOpenWeather = Config.getInstance().getConfigOpenWeather();
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action)
  {
    if ("checkopenweather".equals(action))
    {
      return check();
    }
    return null;
  }

  public String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
    switch (step)
    {
      case "editopenweather":
        return show();
      case "saveopenweather":
        return save();
    }
    return "";
  }

  public String save()
  {
    configOpenWeather.setApplicationId(formValues.getOrDefault("appid", ""));
    configOpenWeather.setCityId(formValues.getOrDefault("location", ""));
    dialogHelper.getActivityFromForm(formValues);
    configOpenWeather.setActivity(dialogHelper.getActivityFromForm(formValues));
    configOpenWeather.setConfigExport(dialogHelper.getDataExporterFromForm(formValues));
    dialogHelper.saveConfiguration();
    ThreadHelper.changedOpenWeatherConfiguration();
    return "";
  }

  public AjaxResult check()
  {
    configOpenWeather.setApplicationId(formValues.getOrDefault("appid", ""));
    configOpenWeather.setCityId(formValues.getOrDefault("location", ""));
    Pair pair = new NetworkConnection().testUrl(configOpenWeather.getApiUrl());
    return new AjaxResult("200".equals(pair.getKey()), pair.getValue());
  }

  public String show()
  {
    Map<String, String> map = new HashMap<>();
    map.put("[appid]", configOpenWeather.getApplicationId());
    map.put("[location]", configOpenWeather.getCityId());
    dialogHelper.setActivityValues(map, configOpenWeather.getActivity());
    dialogHelper.setDataExporter(map, configOpenWeather.getConfigExport());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "openweathermodal.tpl").getHtmlCode(map);
  }

}
