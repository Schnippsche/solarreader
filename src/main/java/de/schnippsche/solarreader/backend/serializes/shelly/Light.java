package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Light
{

  @SerializedName("ison")
  public Boolean ison;
  @SerializedName("source")
  public String source;
  @SerializedName("has_timer")
  public Boolean hasTimer;
  @SerializedName("timer_started")
  public Integer timerStarted;
  @SerializedName("timer_duration")
  public Integer timerDuration;
  @SerializedName("timer_remaining")
  public Integer timerRemaining;
  @SerializedName("mode")
  public String mode;
  @SerializedName("red")
  public Integer red;
  @SerializedName("green")
  public Integer green;
  @SerializedName("blue")
  public Integer blue;
  @SerializedName("white")
  public Integer white;
  @SerializedName("gain")
  public Integer gain;
  @SerializedName("temp")
  public Integer temp;
  @SerializedName("brightness")
  public Integer brightness;
  @SerializedName("effect")
  public Integer effect;
  @SerializedName("transition")
  public Integer transition;

}
