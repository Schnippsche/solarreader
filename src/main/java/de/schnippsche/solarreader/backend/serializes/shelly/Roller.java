package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Roller
{

  @SerializedName("state")
  public String state;
  @SerializedName("source")
  public String source;
  @SerializedName("power")
  public BigDecimal power;
  @SerializedName("is_valid")
  public Boolean isValid;
  @SerializedName("safety_switch")
  public Boolean safetySwitch;
  @SerializedName("overtemperature")
  public Boolean overtemperature;
  @SerializedName("stop_reason")
  public String stopReason;
  @SerializedName("last_direction")
  public String lastDirection;
  @SerializedName("current_pos")
  public Integer currentPos;
  @SerializedName("calibrating")
  public Boolean calibrating;
  @SerializedName("positioning")
  public Boolean positioning;

}
