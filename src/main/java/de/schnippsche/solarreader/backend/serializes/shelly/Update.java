package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

public class Update
{

  @SerializedName("status")
  public String status;
  @SerializedName("has_update")
  public Boolean hasUpdate;
  @SerializedName("new_version")
  public String newVersion;
  @SerializedName("old_version")
  public String oldVersion;
  @SerializedName("beta_version")
  public String betaVersion;

}
