package de.schnippsche.solarreader.backend.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * class for handling array of DayValues
 */
public class DayValues
{
  private final List<DayValue> dayValueList;

  public DayValues()
  {
    dayValueList = new ArrayList<>();
  }

  /**
   * returns the DayValue for a specific name
   *
   * @param name the name to search for
   * @return DayValue if found or new DayValue instance
   */
  public DayValue getDayValue(String name)
  {
    for (DayValue dayValue : dayValueList)
    {
      if (Objects.equals(dayValue.getName(), name))
      {
        return dayValue;
      }
    }
    DayValue newValue = new DayValue(name);
    dayValueList.add(newValue);
    return newValue;
  }

}
