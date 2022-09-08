package de.schnippsche.solarreader.backend.utils;

import org.tinylog.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class Activity
{
  private boolean enabled;
  private int interval;
  private TimeUnit timeUnit;
  private LocalTime startTime;
  private LocalTime endTime;
  private transient LocalDateTime lastCall;
  private transient boolean active;

  public Activity()
  {
    enabled = true;
    interval = 60;
    timeUnit = TimeUnit.SECONDS;
    startTime = LocalTime.of(5, 0, 0);
    endTime = LocalTime.of(23, 0, 0);
    lastCall = null;
    active = false;
  }

  public int getInterval()
  {
    return interval;
  }

  public void setInterval(int interval)
  {
    this.interval = interval;
  }

  public TimeUnit getTimeUnit()
  {
    return timeUnit;
  }

  public void setTimeUnit(TimeUnit timeUnit)
  {
    this.timeUnit = timeUnit;
  }

  public LocalTime getStartTime()
  {
    return startTime;
  }

  public void setStartTime(LocalTime startTime)
  {
    this.startTime = startTime;
  }

  public LocalTime getEndTime()
  {
    return endTime;
  }

  public void setEndTime(LocalTime endTime)
  {
    this.endTime = endTime;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  public LocalDateTime getLastCall()
  {
    return lastCall;
  }

  public void setActive(boolean active)
  {
    this.active = active;
    if (active)
    {
      lastCall = LocalDateTime.now();
    }
  }

  public boolean mustExecute(LocalTime currentTime)
  {
    LocalTime lastCallTime = (lastCall == null) ? LocalTime.MIDNIGHT : lastCall.toLocalTime();
    long timeDiff = Math.abs(Duration.between(currentTime, lastCallTime).toMillis());
    boolean execute = enabled && currentTime.isAfter(startTime) && currentTime.isBefore(endTime) && timeDiff >= timeUnit.toMillis(interval);
    if (execute && active)
    {
      Logger.warn("Process is currently running.... {}", toString());
      return false;
    }
    return execute;
  }

  public void finish()
  {
    lastCall = LocalDateTime.now();
    active = false;
  }

  @Override public String toString()
  {
    return String.format("Activity{enabled=%s, interval=%d, timeUnit=%s, startTime=%s, endTime=%s, lastCall=%s, active=%s}", enabled, interval, timeUnit, startTime, endTime, lastCall, active);
  }

}
