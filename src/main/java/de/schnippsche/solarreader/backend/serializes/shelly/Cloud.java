package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Cloud
{

  @SerializedName("enabled")
  public Boolean enabled;
  @SerializedName("connected")
  public Boolean connected;

}
