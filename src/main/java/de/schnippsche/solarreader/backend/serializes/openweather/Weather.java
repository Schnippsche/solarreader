package de.schnippsche.solarreader.backend.serializes.openweather;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Weather implements ResultFieldConverter
{
  protected Integer id; // Weather condition id
  protected String main; // Group of weather parameters (Rain, Snow, Extreme etc.)
  protected String description; // Weather condition within the group
  protected String icon; // Weather icon id

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "weatherid":
        return new ResultField(deviceField, id);
      case "weathermain":
        return new ResultField(deviceField, main);
      case "weatherdescription":
        return new ResultField(deviceField, description);
      case "weathericon":
        return new ResultField(deviceField, icon);
      default:
        return null;
    }
  }

  @Override public String toString()
  {
    return "Weather{" + "id=" + id + ", main='" + main + '\'' + ", description='" + description + '\'' + ", icon='" + icon + '\'' + '}';
  }

}
