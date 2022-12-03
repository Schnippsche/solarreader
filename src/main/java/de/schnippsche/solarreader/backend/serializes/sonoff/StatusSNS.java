package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StatusSNS implements ResultFieldConverter
{
  //
  public final Map<String, SensorValue> sensors;

  @SerializedName("Time")
  public String time;

  @SerializedName("Temperature")
  public BigDecimal temperature;

  @SerializedName("Humidity")
  public BigDecimal humidity;

  @SerializedName("Light")
  public BigDecimal light;

  @SerializedName("Noise")
  public BigDecimal noise;

  @SerializedName("AirQuality")
  public BigDecimal airQuality;

  @SerializedName("TempUnit")
  public String tempUnit;

  @SerializedName("Switch1")
  public String switch1;

  @SerializedName("Switch2")
  public String switch2;

  public StatusSNS()
  {
    sensors = new HashMap<>();
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "sns_Temperature":
        return new ResultField(deviceField, temperature);
      case "sns_Humidity":
        return new ResultField(deviceField, humidity);
      case "sns_Light":
        return new ResultField(deviceField, light);
      case "sns_Noise":
        return new ResultField(deviceField, noise);
      case "sns_AirQuality":
        return new ResultField(deviceField, airQuality);
      case "sns_TempUnit":
        return new ResultField(deviceField, tempUnit);
      case "sns_switch1":
        return new ResultField(deviceField, switch1);
      case "sns_switch2":
        return new ResultField(deviceField, switch2);
      default:
        return null;
    }

  }

}
