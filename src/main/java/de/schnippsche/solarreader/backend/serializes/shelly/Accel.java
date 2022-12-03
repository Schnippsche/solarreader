package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Accel
{
  @SerializedName("tilt")
  public Integer tilt;
  @SerializedName("vibration")
  public Integer vibration;
  @SerializedName("vibration_time")
  public Integer vibrationTime;

}
