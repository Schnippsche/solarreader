package de.schnippsche.solarreader.backend.configuration;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ConfigDatabase
{
  private final String uuid;
  private String description;
  private boolean enabled;
  private String dbName;
  private String host;
  private int port;
  private String user;
  private String password;
  private String version;
  private boolean useSsl;
  private transient LocalDateTime lastCall;

  public ConfigDatabase()
  {
    uuid = UUID.randomUUID().toString();
    description = "";
    dbName = "solarreader";
    host = "localhost";
    enabled = false;
    version = null;
    port = 8086;
    useSsl = false;
  }

  public ConfigDatabase(ConfigDatabase other)
  {
    uuid = other.uuid;
    description = other.description;
    dbName = other.dbName;
    host = other.host;
    enabled = other.enabled;
    version = other.version;
    port = other.port;
    useSsl = other.useSsl;
    lastCall = other.lastCall;
    user = other.user;
    password = other.password;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  public String getDbName()
  {
    return dbName;
  }

  public void setDbName(String dbName)
  {
    this.dbName = dbName;
  }

  public String getHost()
  {
    return host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  public String getUser()
  {
    return user;
  }

  public void setUser(String user)
  {
    this.user = user;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public boolean isUseSsl()
  {
    return useSsl;
  }

  public void setUseSsl(boolean useSsl)
  {
    this.useSsl = useSsl;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion(String version)
  {
    this.version = version;
  }

  public String getUuid()
  {
    return uuid;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public LocalDateTime getLastCall()
  {
    return lastCall;
  }

  public void setLastCall(LocalDateTime lastCall)
  {
    this.lastCall = lastCall;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    final ConfigDatabase that = (ConfigDatabase) o;
    return uuid.equals(that.uuid);
  }

  @Override public int hashCode()
  {
    return Objects.hash(uuid);
  }

  @Override public String toString()
  {
    return String.format("ConfigDatabase{enabled=%s, dbName='%s', host='%s', port=%d, user='%s', password='%s', useSsl=%s}", enabled, dbName, host, port, user, password, useSsl);
  }

}
