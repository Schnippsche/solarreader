package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Emeter
{
  @SerializedName("power")
  public Integer power;
  @SerializedName("reactive")
  public Integer reactive;
  @SerializedName("voltage")
  public Integer voltage;
  @SerializedName("is_valid")
  public Boolean isValid;
  @SerializedName("total")
  public Integer total;
  @SerializedName("total_returned")
  public Integer totalReturned;

}
