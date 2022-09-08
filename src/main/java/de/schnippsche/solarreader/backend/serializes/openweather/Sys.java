package de.schnippsche.solarreader.backend.serializes.openweather;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Sys implements ResultFieldConverter
{
  private final transient DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
  protected Double type; // Internal parameter
  protected Double id; // Internal parameter
  protected String message; // Internal parameter
  protected String country; // Country code (GB, JP etc.)
  protected Long sunrise; // Sunrise time, unix, UTC
  protected Long sunset; // Sunset time, unix, UTC

  @Override public String toString()
  {
    return "Sys{" + "type=" + type + ", id=" + id + ", message='" + message + '\'' + ", country='" + country + '\'' + ", sunrise=" + sunrise + ", sunset=" + sunset + '}';
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    String time;
    ZonedDateTime zonedDateTime;
    switch (deviceField.getName())
    {
      case "country":
        return new ResultField(deviceField, country);
      case "sunrise":
        return new ResultField(deviceField, sunrise);
      case "sunset":
        return new ResultField(deviceField, sunset);
      case "sunrisetime":
        if (sunrise != null)
        {
          zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(sunrise), ZoneId.of("UTC"));
          time = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).format(dtf);
          return new ResultField(deviceField, time);
        } else
        {
          return null;
        }
      case "sunsettime":
        if (sunset != null)
        {
          zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(sunset), ZoneId.of("UTC"));
          time = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).format(dtf);
          return new ResultField(deviceField, time);
        } else
        {
          return null;
        }
      default:
        return null;
    }
  }

}
