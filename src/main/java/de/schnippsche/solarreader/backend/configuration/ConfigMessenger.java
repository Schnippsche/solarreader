package de.schnippsche.solarreader.backend.configuration;

public class ConfigMessenger
{
  private String apiToken;
  private String apiUser;
  private String apiDevice;
  private String apiMessage;
  private boolean enabled;

  public ConfigMessenger()
  {
    enabled = false;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  public String getApiToken()
  {
    return apiToken;
  }

  public void setApiToken(String apiToken)
  {
    this.apiToken = apiToken;
  }

  public String getApiUser()
  {
    return apiUser;
  }

  public void setApiUser(String apiUser)
  {
    this.apiUser = apiUser;
  }

  public String getApiDevice()
  {
    return apiDevice;
  }

  public void setApiDevice(String apiDevice)
  {
    this.apiDevice = apiDevice;
  }

  public String getApiMessage()
  {
    return apiMessage;
  }

  public void setApiMessage(String apiMessage)
  {
    this.apiMessage = apiMessage;
  }

  @Override public String toString()
  {
    return String.format("ConfigMessenger{ apiToken='%s', apiUser='%s', apiDevice='%s', apiMessage='%s', enabled=%s}", apiToken, apiUser, apiDevice, apiMessage, enabled);
  }

}
