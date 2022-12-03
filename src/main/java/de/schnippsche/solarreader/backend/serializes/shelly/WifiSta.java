package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class WifiSta
{

  @SerializedName("connected")
  public Boolean connected;
  @SerializedName("ssid")
  public String ssid;
  @SerializedName("ip")
  public String ip;
  @SerializedName("rssi")
  public Integer rssi;

}
