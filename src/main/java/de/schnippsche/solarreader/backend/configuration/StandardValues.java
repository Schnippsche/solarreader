package de.schnippsche.solarreader.backend.configuration;

import org.shredzone.commons.suncalc.SunTimes;
import org.tinylog.Logger;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.*;
import java.util.*;

public class StandardValues
{
  public static final String FORMAT_SECONDS = "SECONDS";
  public static final String FORMAT_MILLISECONDS = "MILLISECONDS";
  public static final String FORMAT_MICROSECONDS = "MICROSECONDS";
  public static final String FORMAT_NANOSECONDS = "NANOSECONDS";
  public static final String DATETIME = "DATETIME";
  public static final String DATE = "DATE";
  public static final String TIME = "TIME";
  public static final String HOUR = "HOUR";
  public static final String WEEK = "WEEK";
  public static final String MONTH = "MONTH";
  public static final String YEAR = "YEAR";
  public static final String DAYINMONTH = "DAYINMONTH";
  public static final String DAYINYEAR = "DAYINYEAR";
  public static final String WEEKDAY = "WEEKDAY";
  public static final String CURRENTTIMESTAMP = "CURRENTTIMESTAMP";
  public static final String TODAYTIMESTAMP = "TODAYTIMESTAMP";
  public static final String TODAYLASTYEARTIMESTAMP = "TODAYLASTYEARTIMESTAMP";
  public static final String YESTERDAYTIMESTAMP = "YESTERDAYTIMESTAMP";
  public static final String THISMONTHTIMESTAMP = "THISMONTHTIMESTAMP";
  public static final String THISYEARTIMESTAMP = "THISYEARTIMESTAMP";
  public static final String THISWEEKTIMESTAMP = "THISWEEKTIMESTAMP";
  public static final String LASTMONTHTIMESTAMP = "LASTMONTHTIMESTAMP";
  public static final String LASTYEARTIMESTAMP = "LASTYEARTIMESTAMP";
  public static final String LASTWEEKTIMESTAMP = "LASTWEEKTIMESTAMP";
  public static final String SUNRISE = "SUNRISE";
  public static final String SUNSET = "SUNSET";
  private static final long FACTOR_SECONDS = 1000;
  private static final long FACTOR_MICROSECONDS = 1000;
  private static final long FACTOR_NANOSECONDS = 1000000;
  private final Map<String, Object> stdValues = new Hashtable<>(); // Synchronized table!
  private LocalDate oldDate;
  private LocalTime localTime;
  private LocalDateTime localDateTime;

  public synchronized void putValue(String key, Object value)
  {
    stdValues.put(key, value);
  }

  public Object getValue(String key)
  {
    // look for formatter enclosed in {}
    int start = key.indexOf('{');
    int end = key.indexOf('}');
    if (start >= 0 && end > start)
    {
      String newkey = key.substring(0, start).trim();
      String format = key.substring(start + 1, end).trim();
      return getFormattedValue(newkey, format);
    }
    return stdValues.get(key);
  }

  /**
   * format the value depending on type and format pattern
   *
   * @param key    the standardfield key
   * @param format the format pattern
   * @return formatted value
   */
  public Object getFormattedValue(String key, String format)
  {
    Object value = stdValues.get(key.trim());
    if (value == null)
    {
      return null;
    }
    try
    {
      if (value instanceof TemporalAccessor)
      {
        return DateTimeFormatter.ofPattern(format).format((TemporalAccessor) value);
      }
      if (key.contains("TIMESTAMP"))
      {
        switch (format.trim().toUpperCase())
        {
          case FORMAT_SECONDS:
            return (long) value / FACTOR_SECONDS;
          case FORMAT_MILLISECONDS:
            return value;
          case FORMAT_MICROSECONDS:
            return (long) value * FACTOR_MICROSECONDS;
          case FORMAT_NANOSECONDS:
            return (long) value * FACTOR_NANOSECONDS;
          default:
            Logger.warn("unknown formatter '{}'", format);
            break;
        }
      } else
      {
        Logger.warn("formatter for '{}' supports only date, time, datetime and timestamps", key);
      }
    } catch (Exception e)
    {
      Logger.error("can't format value '{}' for key '{}' with formatter '{}': {}", value, key, format, e.getMessage());
    }
    return value;
  }

  public void reload()
  {
    this.oldDate = null;
  }

  public LocalDateTime getLocalDateTime()
  {
    return localDateTime;
  }

  public long getTimestampSeconds()
  {
    return (Long) getFormattedValue(CURRENTTIMESTAMP, FORMAT_SECONDS);
  }

  public void printValues()
  {
    stdValues.forEach((k, v) -> Logger.info(k + "=" + v));
  }

  public Set<String> getKeys()
  {
    return stdValues.keySet();
  }

  public synchronized void setDateAndTimeValues()
  {
    localDateTime = LocalDateTime.now();
    localTime = localDateTime.toLocalTime();
    LocalDate date = localDateTime.toLocalDate();
    putValue(DATETIME, localDateTime);
    putValue(TIME, localTime);
    putValue(DATE, date);
    putValue(HOUR, localDateTime.getHour());
    long currentMS = System.currentTimeMillis();
    putValue(CURRENTTIMESTAMP, currentMS);
    putValue(HOUR, localDateTime.getHour());
    if (oldDate == null || !date.isEqual(oldDate))
    {
      ZoneId zoneId = ZoneId.systemDefault();
      TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
      int month = date.getMonthValue();
      int year = date.getYear();
      int day = date.getDayOfMonth();
      putValue(WEEK, date.get(woy));
      putValue(MONTH, date.getMonthValue());
      putValue(YEAR, date.getYear());
      putValue(DAYINMONTH, date.getDayOfMonth());
      putValue(DAYINYEAR, date.getDayOfYear());
      putValue(WEEKDAY, date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY));
      long ts = LocalDate.of(year, month, day).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(TODAYTIMESTAMP, ts);
      ts = LocalDate.of(year - 1, month, day).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(TODAYLASTYEARTIMESTAMP, ts);
      ts = date.minusDays(1L).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(YESTERDAYTIMESTAMP, ts);
      ts = date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(THISMONTHTIMESTAMP, ts);
      ts = date.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(THISYEARTIMESTAMP, ts);
      ts = date.with(DayOfWeek.MONDAY).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(THISWEEKTIMESTAMP, ts);
      ts = date.with(TemporalAdjusters.firstDayOfMonth()).minus(1L, ChronoUnit.MONTHS).atStartOfDay(zoneId).toInstant()
               .toEpochMilli();
      putValue(LASTMONTHTIMESTAMP, ts);
      ts = LocalDate.of(year - 1, 1, 1).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(LASTYEARTIMESTAMP, ts);
      ts = date.with(DayOfWeek.MONDAY).minusDays(7L).atStartOfDay(zoneId).toInstant().toEpochMilli();
      putValue(LASTWEEKTIMESTAMP, ts);
      SunTimes times = SunTimes.compute().on(date).at(Config.getInstance().getConfigGeneral().getLatitude()
                                                            .doubleValue(), Config.getInstance().getConfigGeneral()
                                                                                  .getLongitude().doubleValue())
                               .execute();
      putValue(SUNRISE, Objects.requireNonNull(times.getRise()));
      putValue(SUNSET, Objects.requireNonNull(times.getSet()));
      oldDate = date;
    }
    Logger.trace("End of setDateAndTime");
  }

}
