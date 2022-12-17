package de.schnippsche.solarreader.backend.serializes.awattar;

import com.google.gson.annotations.SerializedName;

public class Data
{
  @SerializedName("start_timestamp")
  protected Long startTimestamp;

  @SerializedName("end_timestamp")
  protected Long endTimestamp;

  protected Double marketprice;
  protected String unit;

  public Long getStartTimestamp()
  {
    return startTimestamp;
  }

  public Long getEndTimestamp()
  {
    return endTimestamp;
  }

  public Double getMarketprice()
  {
    return marketprice;
  }

  public String getUnit()
  {
    return unit;
  }

  public boolean isInRange(long timestamp)
  {
    return timestamp >= startTimestamp && timestamp < endTimestamp;
  }

  @Override public String toString()
  {
    return "Data{" + "startTimestamp=" + startTimestamp + ", endTimestamp=" + endTimestamp + ", marketprice="
           + marketprice + ", unit='" + unit + '\'' + '}';
  }

}
