package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Bat
{
  @SerializedName("value")
  public BigDecimal value;
  @SerializedName("voltage")
  public BigDecimal voltage;

}
