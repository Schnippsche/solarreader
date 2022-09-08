package de.schnippsche.solarreader.backend.serializes.openweather;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Clouds implements ResultFieldConverter
{
  @SerializedName("all")
  protected Double cloudiness; // Cloudiness, %

  @Override public String toString()
  {
    return "Clouds{" + "all=" + cloudiness + '}';
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    if ("cloudiness".equals(deviceField.getName()))
    {
      return new ResultField(deviceField, cloudiness);
    }
    return null;
  }

}
