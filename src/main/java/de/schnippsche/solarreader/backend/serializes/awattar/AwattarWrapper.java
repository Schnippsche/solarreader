package de.schnippsche.solarreader.backend.serializes.awattar;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import org.tinylog.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for Awattar Json Api call see more on <a
 * href="https://www.awattar.de/services/api">awattar.de</a>
 */
public class AwattarWrapper
{
  protected String object;
  protected ArrayList<Data> data;
  protected String url;

  public String getObject()
  {
    return object;
  }

  public List<Data> getData()
  {
    return data;
  }

  public String getUrl()
  {
    return url;
  }

  @Override public String toString()
  {
    return String.format("AwattarResult{object='%s', data=%s, url='%s'}", object, data, url);
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
      long ts = ldt.plusHours(deviceField.getOffset()).toEpochSecond(ZoneOffset.UTC) * 1000 + 1;
      for (Data dta : data)
      {
        if (dta.isInRange(ts))
        {
          resultFields.add(new ResultField(deviceField, ResultFieldStatus.VALID, dta.getMarketprice()));
          break;
        }
      }
    }

    return resultFields;
  }

}
