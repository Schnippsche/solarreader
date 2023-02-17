package de.schnippsche.solarreader.backend.configuration;

import org.tinylog.Logger;

import java.math.BigDecimal;

public class ConfigGeneral
{

  protected BigDecimal longitude;
  protected BigDecimal latitude;
  protected Integer influxPushSecondsTimeout;
  protected Integer statisticInterval;

  public ConfigGeneral()
  {
    // Mittelpunkt Deutschlands nach dem 3D-Modell
    longitude = new BigDecimal("10.106111");
    latitude = new BigDecimal("51.590556");
    influxPushSecondsTimeout = 60 * 30; // 30 minutes default
    statisticInterval = 10; // write every 10 minutes into statistic table
  }

  public BigDecimal getLongitude()
  {
    return longitude;
  }

  public void setLongitude(String longitude)
  {
    try
    {
      this.longitude = new BigDecimal(longitude);
    } catch (NumberFormatException e)
    {
      Logger.error("longitude {} is not valid:", longitude);
    }
  }

  public BigDecimal getLatitude()
  {
    return latitude;
  }

  public void setLatitude(String latitude)
  {
    try
    {
      this.latitude = new BigDecimal(latitude);
    } catch (NumberFormatException e)
    {
      Logger.error("latitude {} is not valid:", latitude);
    }
  }

  public int getInfluxPushSecondsTimeout()
  {
    return (influxPushSecondsTimeout != null) ? influxPushSecondsTimeout : 60 * 30;
  }

  public int getStatisticInterval()
  {
    return (statisticInterval != null) ? statisticInterval : 10;
  }

}
