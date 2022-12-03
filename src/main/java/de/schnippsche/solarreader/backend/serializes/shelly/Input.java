package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Input
{

  @SerializedName("input")
  public Integer input;
  @SerializedName("event")
  public String event;
  @SerializedName("event_cnt")
  public Integer eventCnt;
  @SerializedName("last_sequence")
  public String lastSequence;

}
