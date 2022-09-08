package de.schnippsche.solarreader.backend.serializes.openweather;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Main implements ResultFieldConverter
{
  /**
   * Temperature, Unit Celsius
   */
  protected Double temp;
  /**
   * Temperature. This temperature parameter accounts for the human perception of weather.
   */
  @SerializedName("feels_like")
  protected Double feelsLike;
  /**
   * Minimum temperature at the moment. This is minimal currently observed temperature (within large
   * megalopolises and urban areas).
   */
  @SerializedName("temp_min")
  protected Double tempMin;
  /**
   * Maximum temperature at the moment. This is maximal currently observed temperature (within large
   * megalopolises and urban areas).
   */
  @SerializedName("temp_max")
  protected Double tempMax;
  /**
   * Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa
   */
  protected Double pressure;
  /**
   * Humidity in %
   */
  protected Double humidity;
  /**
   * Atmospheric pressure on the sea level, hPa
   */
  @SerializedName("sea_level")
  protected Double seaLevel;
  /**
   * Atmospheric pressure on the ground level, hPa
   */
  @SerializedName("grnd_level")
  protected Double grndLevel;

  @Override public String toString()
  {
    return "Main{" + "temp=" + temp + ", feelsLike=" + feelsLike + ", tempMin=" + tempMin + ", tempMax=" + tempMax + ", pressure=" + pressure + ", humidity=" + humidity + ", seaLevel=" + seaLevel + ", grndLevel=" + grndLevel + '}';
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "temperature":
        return new ResultField(deviceField, temp);
      case "feelslike":
        return new ResultField(deviceField, feelsLike);
      case "pressure":
        return new ResultField(deviceField, pressure);
      case "humidity":
        return new ResultField(deviceField, humidity);
      case "tempmin":
        return new ResultField(deviceField, tempMin);
      case "tempmax":
        return new ResultField(deviceField, tempMax);
      case "sealevel":
        return new ResultField(deviceField, seaLevel);
      case "groundlevel":
        return new ResultField(deviceField, grndLevel);
      default:
        return null;
    }
  }

}
