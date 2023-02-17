package de.schnippsche.solarreader.backend.configuration;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ConfigMqtt
{
  private final String uuid;
  private String description;
  private boolean enabled;
  private String host;
  private int port;
  private String user;
  private String password;
  private boolean useSsl;
  private int keepAlive;
  private String topicName;
  private transient LocalDateTime lastCall;

  public ConfigMqtt()
  {
    uuid = UUID.randomUUID().toString();
    description = "";
    enabled = false;
    port = 1883;
    useSsl = false;
    keepAlive = 10;
    topicName = "solarreader";
  }

  public String getUrl()
  {
    return String.format("%s://%s:%s", useSsl ? "ssl" : "tcp", host, port);
  }

  public String getMainTopic()
  {
    if (topicName.endsWith("/"))
    {
      return topicName;
    }
    return topicName + "/";
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
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

  public int getKeepAlive()
  {
    return keepAlive;
  }

  public void setKeepAlive(int keepAlive)
  {
    this.keepAlive = keepAlive;
  }

  public String getTopicName()
  {
    return topicName;
  }

  public void setTopicName(String topicName)
  {
    this.topicName = topicName;
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

  public MqttConnectOptions getMqttOptions()
  {
    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(false);
    if (user != null && !user.isEmpty())
    {
      options.setUserName(user);
    }
    if (password != null && !password.isEmpty())
    {
      options.setPassword(password.toCharArray());
    }
    options.setCleanSession(true);
    options.setConnectionTimeout(10);
    options.setKeepAliveInterval(keepAlive);
    return options;
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
    final ConfigMqtt that = (ConfigMqtt) o;
    return uuid.equals(that.uuid);
  }

  @Override public int hashCode()
  {
    return Objects.hash(uuid);
  }

}
