package de.schnippsche.solarreader.backend.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WattPerDayIntern
{
  protected LocalDateTime lastUpdate;
  protected BigDecimal wattTotal;
  protected BigDecimal wattCurrent;

  public WattPerDayIntern()
  {
    wattTotal = BigDecimal.ZERO;
    wattCurrent = BigDecimal.ZERO;
    lastUpdate = LocalDateTime.now();
  }

}
