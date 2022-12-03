package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Tmp
{
  @SerializedName("value")
  public BigDecimal value;
  @SerializedName("units")
  public String units;
  @SerializedName("tC")
  public BigDecimal tC;
  @SerializedName("tF")
  public BigDecimal tF;
  @SerializedName("is_valid")
  public Boolean isValid;

}
