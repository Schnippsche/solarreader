package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Helper class for dealing with day values.
 * Some devices did not handle summary values, so we add the current value in a json file instead.
 */
public class DayValueWrapper
{
  private static final int SCALE = 6;
  private final File file;
  private final JsonTools jsonTools;
  private DayValues dayValues;

  /**
   * Constructor for dealing with device
   *
   * @param configDevice the configuration for the device
   */
  public DayValueWrapper(ConfigDevice configDevice)
  {
    jsonTools = new JsonTools();
    final String prefix = configDevice.getDeviceClass();
    final String filename = prefix + "_" + configDevice.getUuid() + ".json";
    file = new File(filename);
  }

  /**
   * calculate factor for activity for exact one hour
   * normalize different time units and intervals to one hour interval
   * for example every 10 seconds is 3600/10 = 360 entries per one hour
   * every 2 hours is 1/2 = 0.5,  every 1 minute is 60/1 = 60
   *
   * @param activity the Activity with time unit and interval
   * @return BigDecimal factor for one hour
   */
  public BigDecimal getFactor(Activity activity)
  {
    BigDecimal interval = new BigDecimal(activity.getInterval());
    switch (activity.getTimeUnit())
    {
      case MILLISECONDS:
        return new BigDecimal(3600 * 1000).divide(interval, SCALE, RoundingMode.HALF_EVEN);
      case SECONDS:
        return new BigDecimal(3600).divide(interval, SCALE, RoundingMode.HALF_EVEN);
      case MINUTES:
        return new BigDecimal(60).divide(interval, SCALE, RoundingMode.HALF_EVEN);
      case HOURS:
        return BigDecimal.ONE.divide(interval, SCALE, RoundingMode.HALF_EVEN);
      default:
        return BigDecimal.ZERO;
    }
  }

  /**
   * add a value from a result field
   *
   * @param resultField the resultfield
   * @param factor      the factor for one hour
   * @return BigDecimal summary for one day
   */
  public BigDecimal addValue(ResultField resultField, BigDecimal factor)
  {
    return addValue(resultField.getName(), resultField.getNumericValue(), factor);
  }

  /**
   * add a value with a specific name
   *
   * @param name   the name
   * @param value  the current value
   * @param factor the factor for one hour
   * @return BigDecimal summary for one day
   */
  public BigDecimal addValue(String name, BigDecimal value, BigDecimal factor)
  {
    dayValues = readFromFile(file);
    if (dayValues == null)
    {
      dayValues = new DayValues();
    }
    DayValue dayValue = dayValues.getDayValue(name);
    // if it is another day, reset value
    if (!dayValue.getLastUpdate().toLocalDate().equals(LocalDate.now()))
    {
      dayValue.setTotal(BigDecimal.ZERO);
    }
    BigDecimal newValue = value.divide(factor, SCALE, RoundingMode.HALF_EVEN);
    dayValue.setLastValue(newValue);
    dayValue.setTotal(dayValue.getTotal().add(newValue));
    dayValue.setLastUpdate(LocalDateTime.now());
    saveToFile(file);
    return dayValue.getTotal();
  }

  /**
   * read all data from a json file
   *
   * @param file the specific file
   * @return DayValues instance or null if nothing exist
   */
  private synchronized DayValues readFromFile(File file)
  {
    return jsonTools.getObjectFromFile(file.toPath(), DayValues.class);
  }

  /**
   * save all data to a json file
   *
   * @param file the specific file
   */
  private synchronized void saveToFile(File file)
  {
    jsonTools.saveObjectToFile(file.toPath(), dayValues);
  }

}
