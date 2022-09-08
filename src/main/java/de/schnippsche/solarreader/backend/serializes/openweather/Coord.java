package de.schnippsche.solarreader.backend.serializes.openweather;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Coord implements ResultFieldConverter
{
  @SerializedName("lon")
  protected Double longitude;

  @SerializedName("lat")
  protected Double latitude;

  public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "longitude":
        return new ResultField(deviceField, longitude);
      case "latitude":
        return new ResultField(deviceField, latitude);
      default:
        return null;
    }
  }

  @Override public String toString()
  {
    return "Coord{" + "lon=" + longitude + ", lat=" + latitude + '}';
  }

}
