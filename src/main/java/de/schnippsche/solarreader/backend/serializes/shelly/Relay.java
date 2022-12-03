package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Relay
{
  @SerializedName("ison")
  public Boolean ison;
  @SerializedName("has_timer")
  public Boolean hasTimer;
  @SerializedName("timer_started")
  public Integer timerStarted;
  @SerializedName("timer_duration")
  public Integer timerDuration;
  @SerializedName("timer_remaining")
  public Integer timerRemaining;
  @SerializedName("overpower")
  public Boolean overpower;
  @SerializedName("source")
  public String source;

}
