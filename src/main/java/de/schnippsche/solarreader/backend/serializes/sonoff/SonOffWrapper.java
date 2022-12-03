package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class SonOffWrapper
{

  @SerializedName("Status")
  public Status status;

  @SerializedName("StatusFWR")
  public StatusFWR statusFWR;

  @SerializedName("StatusSNS")
  public StatusSNS statusSNS;

  @SerializedName("StatusSTS")
  public StatusSTS statusSTS;

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
    if (status != null)
    {
      converters.add(status);
    }
    if (statusFWR != null)
    {
      converters.add(statusFWR);
    }

    if (statusSNS != null)
    {
      converters.add(statusSNS);
    }
    if (statusSTS != null)
    {
      converters.add(statusSTS);
    }

    return converters;
  }

}
