package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Sensor
{
  @SerializedName("motion")
  public Boolean motion;
  @SerializedName("vibration")
  public Boolean vibration;
  @SerializedName("timestamp")
  public Integer timestamp;
  @SerializedName("active")
  public Boolean active;
  @SerializedName("state")
  public String state;
  @SerializedName("is_valid")
  public Boolean isValid;

}
