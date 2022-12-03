package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;

public class StatusFWR implements ResultFieldConverter
{

  @SerializedName("Version")
  public String version;

  @SerializedName("BuildDateTime")
  public String buildDateTime;

  @SerializedName("Boot")
  public Integer boot;

  @SerializedName("Core")
  public String core;

  @SerializedName("SDK")
  public String sdk;

  @SerializedName("CpuFrequency")
  public Integer cpuFrequency;

  @SerializedName("Hardware")
  public String hardware;

  @SerializedName("CR")
  public String cr;

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "fwr_Version":
        return new ResultField(deviceField, version);
      case "fwr_BuildDateTime":
        return new ResultField(deviceField, buildDateTime);
      case "fwr_Boot":
        return new ResultField(deviceField, boot);
      case "fwr_Core":
        return new ResultField(deviceField, core);
      case "fwr_Sdk":
        return new ResultField(deviceField, sdk);
      case "fwr_CpuFrequency":
        return new ResultField(deviceField, cpuFrequency);
      case "fwr_Hardware":
        return new ResultField(deviceField, hardware);
      case "fwr_Cr":
        return new ResultField(deviceField, cr);
      default:
        return null;
    }
  }

}
