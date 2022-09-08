package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.utils.Activity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class ConfigSolarprognose
{
  private static final String SOLARPROGNOSE_URL = "http://www.solarprognose.de/web/solarprediction/api/v1?access-token=%s&item=%s&id=%s&type=hourly&_format=json&algorithm=%s&project=solarreader&START_EPOCH_TIME=%s&END_EPOCH_TIME=%s";
  private ConfigExport configExport;
  private Activity activity;
  private String accessToken;
  private String apiId;
  private String apiItem; // plant, inverter
  private String apiAlgorythm;

  public ConfigSolarprognose()
  {
    apiItem = "plant";
    apiAlgorythm = "mosmix";
    apiId = "";
    accessToken = "";
    activity = new Activity();
    activity.setInterval(1);
    activity.setStartTime(LocalTime.of(4, 0, 0));
    activity.setEndTime(LocalTime.of(22, 0, 0));
    activity.setTimeUnit(TimeUnit.HOURS);
    activity.setEnabled(false);
    configExport = new ConfigExport();
  }

  public String getApiUrl()
  {
    long start = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toEpochSecond(ZoneOffset.UTC);
    // Send todays midnight as epoch time to api
    long end = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 0, 0)).toEpochSecond(ZoneOffset.UTC);
    return String.format(SOLARPROGNOSE_URL, accessToken, apiItem, apiId, apiAlgorythm, start, end);
  }

  public String getAccessToken()
  {
    return accessToken;
  }

  public void setAccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }

  public String getApiId()
  {
    return apiId;
  }

  public void setApiId(String apiId)
  {
    this.apiId = apiId;
  }

  public String getApiItem()
  {
    return apiItem;
  }

  public void setApiItem(String apiItem)
  {
    this.apiItem = apiItem;
  }

  public String getApiAlgorythm()
  {
    return apiAlgorythm;
  }

  public void setApiAlgorythm(String apiAlgorythm)
  {
    this.apiAlgorythm = apiAlgorythm;
  }

  public Activity getActivity()
  {
    return activity;
  }

  public void setActivity(Activity activity)
  {
    this.activity = activity;
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
