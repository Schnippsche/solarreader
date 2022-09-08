package de.schnippsche.solarreader.backend.serializes.openweather;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for OpenWeather Json Api call see more on <a
 * href="https://openweathermap.org/current">openweathermap</a>
 */
public class OpenWeatherWrapper implements ResultFieldConverter
{

  protected Coord coord;
  protected ArrayList<Weather> weather;
  protected Main main;
  protected Wind wind;
  protected Clouds clouds;
  protected Rain rain;
  protected Snow snow;
  protected Sys sys;
  // Internal parameter
  protected String base;
  // Visibility, meter. The maximum value of the visibility is 10km
  protected Double visibility;

  @SerializedName("dt")
  protected Long dataCalculationTs; // Time of data calculation, unix, UTC

  @SerializedName("timezone")
  protected Integer timezoneShift; // Shift in seconds from UTC

  @SerializedName("id")
  protected Double cityId;

  @SerializedName("name")
  protected String cityName;

  protected Integer cod; // Internal parameter

  public List<ResultField> getResultFields(List<DeviceField> deviceFields)
  {
    final List<ResultField> resultFields = new ArrayList<>();
    if (deviceFields.isEmpty())
    {
      Logger.warn("empty device field list, skip converting...");
      return resultFields;
    }
    ArrayList<ResultFieldConverter> converters = getConverter();
    for (DeviceField deviceField : deviceFields)
    {
      for (ResultFieldConverter converter : converters)
      {
        ResultField resultField = converter.getResultField(deviceField);
        if (resultField != null && resultField.isValid())
        {
          resultFields.add(resultField);
          break;
        }
      }
    }
    return resultFields;
  }

  private ArrayList<ResultFieldConverter> getConverter()
  {
    ArrayList<ResultFieldConverter> converters = new ArrayList<>();
    converters.add(this);
    if (clouds != null)
    {
      converters.add(clouds);
    }
    if (coord != null)
    {
      converters.add(coord);
    }
    if (main != null)
    {
      converters.add(main);
    }
    if (rain != null)
    {
      converters.add(rain);
    }
    if (snow != null)
    {
      converters.add(snow);
    }
    if (sys != null)
    {
      converters.add(sys);
    }
    if (wind != null)
    {
      converters.add(wind);
    }
    if (!weather.isEmpty())
    {
      converters.add(weather.get(0));
    }
    return converters;
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "visibility":
        return new ResultField(deviceField, visibility);
      case "calculationtime":
        return new ResultField(deviceField, dataCalculationTs);
      case "timezoneshift":
        return new ResultField(deviceField, timezoneShift);
      case "cityid":
        return new ResultField(deviceField, cityId);
      case "cityname":
        return new ResultField(deviceField, cityName);
      default:
        return null;
    }
  }

  @Override public String toString()
  {
    return String.format("OpenWeatherResult{coordObject=%s, weatherObject=%s, mainObject=%s, windObject=%s, cloudsObject=%s, rainObject=%s, snowObject=%s, sysObject=%s, base='%s', visibility=%s, dt=%d, timezone=%d, id=%s, name='%s', cod=%d}", coord, weather, main, wind, clouds, rain, snow, sys, base, visibility, dataCalculationTs, timezoneShift, cityId, cityName, cod);
  }

}
