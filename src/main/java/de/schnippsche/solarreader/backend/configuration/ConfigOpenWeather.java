package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.utils.Activity;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class ConfigOpenWeather
{
  private static final String OPENWEATHER_URL =
    "http://api.openweathermap.org/data/2.5/weather?id=%s&APPID=%s&lang=de&units=metric";
  private ConfigExport configExport;
  private Activity activity;
  private String applicationId;
  private String cityId;

  public ConfigOpenWeather()
  {
    applicationId = "";
    cityId = "";
    activity = new Activity();
    activity.setEnabled(false);
    activity.setInterval(1);
    activity.setStartTime(LocalTime.of(4, 0, 1));
    activity.setEndTime(LocalTime.of(22, 0, 0));
    activity.setTimeUnit(TimeUnit.HOURS);
    configExport = new ConfigExport();
  }

  public String getApiUrl()
  {
    return String.format(OPENWEATHER_URL, cityId, applicationId);
  }

  public Activity getActivity()
  {
    return activity;
  }

  public void setActivity(Activity activity)
  {
    this.activity = activity;
  }

  public String getApplicationId()
  {
    return applicationId;
  }

  public void setApplicationId(String applicationId)
  {
    this.applicationId = applicationId;
  }

  public String getCityId()
  {
    return cityId;
  }

  public void setCityId(String cityId)
  {
    this.cityId = cityId;
  }

  public ConfigExport getConfigExport()
  {
    return configExport;
  }

  public void setConfigExport(ConfigExport configExport)
  {
    this.configExport = configExport;
  }

}
