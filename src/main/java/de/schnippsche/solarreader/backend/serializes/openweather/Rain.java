package de.schnippsche.solarreader.backend.serializes.openweather;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Rain implements ResultFieldConverter
{
  @SerializedName("1h")
  protected Double oneHour; // Rain volume for the last 1 hour, mm

  @SerializedName("3h")
  protected Double threeHour; // Rain volume for the last 3 hours, mm

  @Override public String toString()
  {
    return "Rain{" + "oneHour=" + oneHour + ", threeHour=" + threeHour + '}';
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "rainonehour":
        return new ResultField(deviceField, oneHour);
      case "rainthreehour":
        return new ResultField(deviceField, threeHour);
      default:
        return null;
    }
  }

}
