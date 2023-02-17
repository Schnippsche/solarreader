package de.schnippsche.solarreader.backend.utils;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

public class ExpiringCommand implements Comparable<ExpiringCommand>
{
  private final LocalDateTime commandTime;
  private final String uuid;
  private final String command;
  private final String value;
  private final int durationMinutes;

  public ExpiringCommand(String uuid, String command)
  {
    this(uuid, command, "");
  }

  public ExpiringCommand(String uuid, String command, String value)
  {
    this(uuid, command, value, 5);
  }

  public ExpiringCommand(String uuid, String command, String value, int durationMinutes)
  {
    this.commandTime = LocalDateTime.now();
    this.uuid = uuid;
    this.command = command;
    this.value = value;
    this.durationMinutes = durationMinutes;
  }

  public boolean isValid()
  {
    return MINUTES.between(commandTime, LocalDateTime.now()) <= durationMinutes;
  }

  public LocalDateTime getCommandTime()
  {
    return commandTime;
  }

  public String getUuid()
  {
    return uuid;
  }

  public String getCommand()
  {
    return command;
  }

  public String getValue()
  {
    return value;
  }

  @Override public String toString()
  {
    return "ExpiringCommand{" + "commandTime=" + commandTime + ", uuid='" + uuid + '\'' + ", command='" + command + '\''
           + ", value='" + value + '\'' + ", durationMinutes=" + durationMinutes + '}';
  }

  @Override public int compareTo(@NotNull ExpiringCommand o)
  {
    return commandTime.compareTo(o.commandTime);
  }

}
