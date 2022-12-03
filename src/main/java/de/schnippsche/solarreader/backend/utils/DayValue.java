package de.schnippsche.solarreader.backend.utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DayValue
{
  private final String name;
  private LocalDateTime lastUpdate;
  private BigDecimal total;
  private BigDecimal lastValue;

  public DayValue(String name)
  {
    this.name = name;
    total = BigDecimal.ZERO;
    lastValue = BigDecimal.ZERO;
    lastUpdate = LocalDateTime.now();
  }

  public String getName()
  {
    return name;
  }

  public LocalDateTime getLastUpdate()
  {
    return lastUpdate;
  }

  public void setLastUpdate(LocalDateTime lastUpdate)
  {
    this.lastUpdate = lastUpdate;
  }

  public BigDecimal getTotal()
  {
    return total;
  }

  public void setTotal(BigDecimal total)
  {
    this.total = total;
  }

  public BigDecimal getLastValue()
  {
    return lastValue;
  }

  public void setLastValue(BigDecimal lastValue)
  {
    this.lastValue = lastValue;
  }

}
