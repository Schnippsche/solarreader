package de.schnippsche.solarreader.backend.serializes.solarprognose;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import org.tinylog.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for Solarprognose Json Api call see more on <a
 * href="https://www.solarprognose.de">solarprognose.de</a>
 */
public class SolarprognoseWrapper
{
  protected PreferredNextApiRequestAt preferredNextApiRequestAt;
  protected Integer status;

  @SerializedName("iLastPredictionGenerationEpochTime")
  protected Integer lastPredictionGenerationEpochTime;

  @SerializedName("weather_source_text")
  protected String weatherSourceText;

  protected String datalinename;
  protected HashMap<Long, List<Double>> data;

  public Integer getStatus()
  {
    return status;
  }

  public Integer getLastPredictionGenerationEpochTime()
  {
    return lastPredictionGenerationEpochTime;
  }

  public String getWeatherSourceText()
  {
    return weatherSourceText;
  }

  public String getDatalinename()
  {
    return datalinename;
  }

  public Map<Long, List<Double>> getData()
  {
    return data;
  }

  public List<ResultField> getResultFields(List<DeviceField> deviceFields)
  {
    final List<ResultField> resultFields = new ArrayList<>();
    if (deviceFields.isEmpty())
    {
      Logger.warn("empty device field list, skip converting...");
      return resultFields;
    }
    if (data == null)
    {
      Logger.warn("empty data, skip converting...");
      return resultFields;
    }
    LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
    for (DeviceField deviceField : deviceFields)
    {
      // convert hour into timestamp
      long ts = ldt.plusHours(deviceField.getOffset()).toEpochSecond(ZoneOffset.UTC);
      List<Double> values = data.get(ts);
      if (values != null && values.size() > 1)
      {
        Double value = deviceField.getName().contains("accumulated") ? values.get(1) : values.get(0);
        if (value != null)
        {
          resultFields.add(new ResultField(deviceField, ResultFieldStatus.VALID, value));
        }
      }
    }

    return resultFields;
  }

  @Override public String toString()
  {
    return String.format("SolarprognoseResult{preferredNextApiRequestAt=%s, status=%d, LastPredictionGenerationEpochTime=%d, weatherSourceText='%s', datalinename='%s', data=%s}", preferredNextApiRequestAt, status, lastPredictionGenerationEpochTime, weatherSourceText, datalinename, data);
  }

}
