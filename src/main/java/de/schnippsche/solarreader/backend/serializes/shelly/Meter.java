package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class Meter
{
  @SerializedName("power")
  public BigDecimal power;
  @SerializedName("overpower")
  public BigDecimal overpower;
  @SerializedName("is_valid")
  public Boolean isValid;
  @SerializedName("timestamp")
  public Integer timestamp;
  @SerializedName("counters")
  public List<Integer> counters = null;
  @SerializedName("total")
  public Integer total;

}
