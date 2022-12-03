package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Hum
{

  @SerializedName("value")
  public BigDecimal value;
  @SerializedName("is_valid")
  public Boolean isValid;

}
