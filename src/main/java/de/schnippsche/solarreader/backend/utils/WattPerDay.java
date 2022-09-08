package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import org.tinylog.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WattPerDay
{
  private static final int SCALE = 6;
  private final BigDecimal factor;
  private final File file;
  private final JsonTools jsonTools;
  private WattPerDayIntern intern;

  public WattPerDay(ConfigDevice configDevice)
  {
    String prefix = configDevice.getDeviceClass();
    jsonTools = new JsonTools();
    intern = new WattPerDayIntern();
    intern.wattTotal = BigDecimal.ZERO;
    String filename = prefix + "_" + configDevice.getUuid() + ".json";
    file = new File(filename);
    BigDecimal interval = new BigDecimal(configDevice.getActivity().getInterval());
    switch (configDevice.getActivity().getTimeUnit())
    {
      case MILLISECONDS:
        factor = new BigDecimal(3600 * 1000).divide(interval, SCALE, RoundingMode.HALF_EVEN);
        break;
      case SECONDS:
        factor = new BigDecimal(3600).divide(interval, SCALE, RoundingMode.HALF_EVEN);
        break;
      case MINUTES:
        factor = new BigDecimal(60).divide(interval, SCALE, RoundingMode.HALF_EVEN);
        break;
      case HOURS:
        factor = BigDecimal.ONE.divide(interval, SCALE, RoundingMode.HALF_EVEN);
        break;
      default:
        Logger.warn("not supported time unit '{}' for watt per day calculating!", configDevice.getActivity()
                                                                                              .getTimeUnit());
        factor = BigDecimal.ZERO;
    }
  }

  public BigDecimal addWattHours(BigDecimal wattHoursCurrent)
  {
    readFromFile(file);
    // if it is another day, reset value
    if (!intern.lastUpdate.toLocalDate().equals(LocalDate.now()))
    {
      intern.wattTotal = BigDecimal.ZERO;
    }
    intern.wattCurrent = wattHoursCurrent;
    BigDecimal wattHour = intern.wattCurrent.divide(factor, SCALE, RoundingMode.HALF_EVEN);
    intern.wattTotal = intern.wattTotal.add(wattHour);
    intern.lastUpdate = LocalDateTime.now();
    saveToFile(file);
    return intern.wattTotal;
  }

  public BigDecimal addWattHours(ResultField wattResultField)
  {
    BigDecimal tmp = wattResultField != null && wattResultField.isValid() ? wattResultField.getNumericValue() : BigDecimal.ZERO;
    return addWattHours(tmp);
  }

  private void readFromFile(File file)
  {
    intern = jsonTools.getObjectFromFile(file.toPath(), WattPerDayIntern.class);
    if (intern == null)
    {
      intern = new WattPerDayIntern();
    }
  }

  private void saveToFile(File file)
  {
    jsonTools.saveObjectToFile(file.toPath(), intern);
  }

}
