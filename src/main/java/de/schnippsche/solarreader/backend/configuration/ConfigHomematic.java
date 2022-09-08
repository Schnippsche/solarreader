package de.schnippsche.solarreader.backend.configuration;

public class ConfigHomematic
{

  private String ip;
  private boolean enabled;

  public ConfigHomematic()
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

  public String getIp()
  {
    return ip;
  }

  public void setIp(String ip)
  {
    this.ip = ip;
  }

  @Override public String toString()
  {
    return String.format("ConfigHomematic{ ip='%s', enbled=%s}", ip, enabled);
  }

}
