package de.schnippsche.solarreader.backend.utils;

import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

  public Activity(Activity other)
  {
    this.enabled = other.enabled;
    this.interval = other.interval;
    this.timeUnit = other.timeUnit;
    this.startTime = other.startTime;
    this.endTime = other.endTime;
    this.lastCall = other.lastCall;
    this.active = other.active;
  }

  public void changeValues(Activity newActivity)
  {
    setEnabled(newActivity.isEnabled());
    setInterval(newActivity.getInterval());
    setTimeUnit(newActivity.getTimeUnit());
    setStartTime(newActivity.getStartTime());
    setEndTime(newActivity.getEndTime());
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

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  public void setLastCall()
  {
    lastCall = LocalDateTime.now();
  }

  public boolean mustExecute(LocalDateTime currentTime)
  {
    LocalDateTime lastCallTime = (lastCall == null) ? LocalDateTime.now().minusHours(24) : lastCall;
    long timeDiff = Math.abs(lastCallTime.until(currentTime, ChronoUnit.SECONDS));
    LocalTime localTime = currentTime.toLocalTime();
    boolean execute = enabled && localTime.isAfter(startTime) && localTime.isBefore(endTime)
                      && timeDiff >= timeUnit.toSeconds(interval);
    if (execute && active)
    {
      Logger.warn("Process is currently running since {} seconds .... {}", timeDiff, toString());
      return false;
    }
    return execute;
  }

  public void finish()
  {
    setLastCall();
    active = false;
  }

  @Override public String toString()
  {
    return String.format("Activity{enabled=%s, interval=%d, timeUnit=%s, startTime=%s, endTime=%s, lastCall=%s, active=%s}", enabled, interval, timeUnit, startTime, endTime, lastCall, active);
  }

}
