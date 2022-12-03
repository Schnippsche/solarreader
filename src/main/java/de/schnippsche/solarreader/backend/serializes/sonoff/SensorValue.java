package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;

import java.math.BigDecimal;

/**
 * class for holding different types of sensors
 * elements depends on sensor type, for example DS18B20 holds only temperature and id
 */
public class SensorValue implements ResultFieldConverter
{

  public String sensorName;

  @SerializedName("Id")
  public String id;

  @SerializedName("Temperature")
  public BigDecimal temperature;

  @SerializedName("Humidity")
  public BigDecimal humidity;

  @SerializedName("DewPoint")
  public BigDecimal dewPoint;

  @SerializedName("Total")
  public BigDecimal total;

  @SerializedName("TotalStartTime")
  public String totalStartTime;

  @SerializedName("Period")
  public BigDecimal period;

  @SerializedName("ApparentPower")
  public BigDecimal apparentPower;

  @SerializedName("ReactivePower")
  public BigDecimal reactivePower;

  @SerializedName("Yesterday")
  public BigDecimal yesterday;

  @SerializedName("Today")
  public BigDecimal today;

  @SerializedName("Power")
  public BigDecimal power;

  @SerializedName("Factor")
  public BigDecimal factor;

  @SerializedName("Voltage")
  public BigDecimal voltage;

  @SerializedName("Current")
  public BigDecimal current;

  @SerializedName("CF1")
  public BigDecimal cf1;

  @SerializedName("CF25")
  public BigDecimal cf25;

  @SerializedName("CF10")
  public BigDecimal cf10;

  @SerializedName("PM1")
  public BigDecimal pm1;

  @SerializedName("PM25")
  public BigDecimal pm25;

  @SerializedName("PM10")
  public BigDecimal pm10;

  @SerializedName("PB03")
  public BigDecimal pb03;

  @SerializedName("PB05")
  public BigDecimal pb05;

  @SerializedName("PB1")
  public BigDecimal pb1;

  @SerializedName("PB25")
  public BigDecimal pb25;

  @SerializedName("PB5")
  public BigDecimal pb5;

  @SerializedName("PB10")
  public BigDecimal pb10;

  public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "sensor_Name":
        return new ResultField(deviceField, sensorName);
      case "sensor_Id":
        return new ResultField(deviceField, id);
      case "sensor_Temperature":
        return new ResultField(deviceField, temperature);
      case "sensor_Humidity":
        return new ResultField(deviceField, humidity);
      case "sensor_DewPoint":
        return new ResultField(deviceField, dewPoint);
      case "sensor_Total":
        return new ResultField(deviceField, total);
      case "sensor_TotalStartTime":
        return new ResultField(deviceField, totalStartTime);
      case "sensor_Period":
        return new ResultField(deviceField, period);
      case "sensor_ApparentPower":
        return new ResultField(deviceField, apparentPower);
      case "sensor_ReactivePower":
        return new ResultField(deviceField, reactivePower);
      case "sensor_Yesterday":
        return new ResultField(deviceField, yesterday);
      case "sensor_Today":
        return new ResultField(deviceField, today);
      case "sensor_Power":
        return new ResultField(deviceField, power);
      case "sensor_Factor":
        return new ResultField(deviceField, factor);
      case "sensor_Voltage":
        return new ResultField(deviceField, voltage);
      case "sensor_Current":
        return new ResultField(deviceField, current);
      case "sensor_CF1":
        return new ResultField(deviceField, cf1);
      case "sensor_CF25":
        return new ResultField(deviceField, cf25);
      case "sensor_CF10":
        return new ResultField(deviceField, cf10);
      case "sensor_PM1":
        return new ResultField(deviceField, pm1);
      case "sensor_PM25":
        return new ResultField(deviceField, pm25);
      case "sensor_PM10":
        return new ResultField(deviceField, pm10);
      case "sensor_PB03":
        return new ResultField(deviceField, pb03);
      case "sensor_PB05":
        return new ResultField(deviceField, pb05);
      case "sensor_PB1":
        return new ResultField(deviceField, pb1);
      case "sensor_PB25":
        return new ResultField(deviceField, pb25);
      case "sensor_PB5":
        return new ResultField(deviceField, pb5);
      case "sensor_PB10":
        return new ResultField(deviceField, pb10);
      default:
        return null;
    }
  }

}
