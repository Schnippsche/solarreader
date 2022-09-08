package de.schnippsche.solarreader.backend.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateTimeHelper
{
  public String convertTimestamp(long timestampSeconds, String format)
  {
    LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestampSeconds), TimeZone.getDefault()
                                                                                                 .toZoneId());
    return ldt.format(DateTimeFormatter.ofPattern(format));
  }

}
