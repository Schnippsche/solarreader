package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.utils.Activity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class ConfigAwattar
{
  private static final String AWATTAR_URL = "https://api.awattar.%s/v1/marketdata?start=%s";
  private final String country;
  private ConfigExport configExport;
  private BigDecimal priceCorrection;
  private Activity activity;

  public ConfigAwattar()
  {
    activity = new Activity();
    activity.setInterval(1);
    activity.setTimeUnit(TimeUnit.HOURS);
    activity.setStartTime(LocalTime.of(1, 0, 0));
    activity.setEndTime(LocalTime.of(3, 0, 0));
    activity.setEnabled(false);
    priceCorrection = BigDecimal.ZERO;
    country = "de";
    configExport = new ConfigExport();
  }

  public String getApiUrl()
  {
    LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    // Send todays midnight as epoch time to api
    return String.format(AWATTAR_URL, country, ldt.toEpochSecond(ZoneOffset.UTC) * 1000);
  }

  public Activity getActivity()
  {
    return activity;
  }

  public void setActivity(Activity activity)
  {
    this.activity = activity;
  }

  public BigDecimal getPriceCorrection()
  {
    return priceCorrection;
  }

  public void setPriceCorrection(BigDecimal priceCorrection)
  {
    this.priceCorrection = priceCorrection;
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
