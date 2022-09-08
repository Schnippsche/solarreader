package de.schnippsche.solarreader.backend.configuration;

import java.math.BigDecimal;

public class ConfigGeneral
{

  protected BigDecimal longitude;
  protected BigDecimal latitude;
  protected Integer influxPushSecondsTimeout;
  protected Integer mqttPushSecondsTimeout;
  protected Integer statisticInterval;

  public ConfigGeneral()
  {
    longitude = new BigDecimal("7.507051");
    latitude = new BigDecimal("49.798903");
    influxPushSecondsTimeout = 60 * 30; // 30 minutes default
    mqttPushSecondsTimeout = 10; // 10 seconds default for mqtt
    statisticInterval = 10; // write every 10 minutes into statistic table
  }

  public BigDecimal getLongitude()
  {
    return longitude;
  }

  public BigDecimal getLatitude()
  {
    return latitude;
  }

  public int getInfluxPushSecondsTimeout()
  {
    return (influxPushSecondsTimeout != null) ? influxPushSecondsTimeout : 60 * 30;
  }

  public int getMqttPushSecondsTimeout()
  {
    return (mqttPushSecondsTimeout != null) ? mqttPushSecondsTimeout : 10;
  }

  public int getStatisticInterval()
  {
    return (statisticInterval != null) ? statisticInterval : 10;
  }

}
