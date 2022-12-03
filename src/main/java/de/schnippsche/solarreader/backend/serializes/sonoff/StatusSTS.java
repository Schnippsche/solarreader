package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;

public class StatusSTS implements ResultFieldConverter
{

  @SerializedName("Time")
  public String time;

  @SerializedName("Uptime")
  public String uptime;

  @SerializedName("UptimeSec")
  public Integer uptimeSec;

  @SerializedName("Heap")
  public Integer heap;

  @SerializedName("SleepMode")
  public String sleepMode;

  @SerializedName("Sleep")
  public Integer sleep;

  @SerializedName("LoadAvg")
  public Integer loadAvg;

  @SerializedName("MqttCount")
  public Integer mqttCount;

  @SerializedName("POWER")
  public String power;

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "sts_Time":
        return new ResultField(deviceField, time);
      case "sts_Uptime":
        return new ResultField(deviceField, uptime);
      case "sts_UptimeSec":
        return new ResultField(deviceField, uptimeSec);
      case "sts_Heap":
        return new ResultField(deviceField, heap);
      case "sts_SleepMode":
        return new ResultField(deviceField, sleepMode);
      case "sts_Sleep":
        return new ResultField(deviceField, sleep);
      case "sts_LoadAvg":
        return new ResultField(deviceField, loadAvg);
      case "sts_MqttCount":
        return new ResultField(deviceField, mqttCount);
      case "sts_Power":
        return new ResultField(deviceField, "ON".equals(power) ? "1" : "0");
      default:
        return null;
    }
  }

}
