package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.JsonTools;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * this class checks periodically if a new version is available
 */
public class UpdateWorker implements Runnable
{
  private static final String SOLARREADER_VERSION_URL = "https://www.solarreader.de/version.json";
  private final JsonTools jsonTools;
  private final NumericHelper numericHelper;
  private final Activity activity;

  public UpdateWorker()
  {
    jsonTools = new JsonTools();
    activity = new Activity();
    activity.setInterval(3);
    activity.setTimeUnit(TimeUnit.HOURS);
    numericHelper = new NumericHelper();
  }

  @Override public void run()
  {
    if (activity.mustExecute(LocalDateTime.now()))
    {
      activity.setActive(true);
      activity.setLastCall();
      if (checkForUpdate())
      {
        SolarMain.updateAvailable = true;
      }
      activity.finish();
    }
  }

  public Activity getActivity()
  {
    return activity;
  }

  public boolean checkForUpdate()
  {
    Map<String, Object> maps = jsonTools.getSimpleMapFromUrl(SOLARREADER_VERSION_URL, null);
    if (maps != null)
    {
      BigDecimal currentVersion = numericHelper.getBigDecimal(SolarMain.softwareVersion);
      BigDecimal newVersion = numericHelper.getBigDecimal(maps.getOrDefault("version", "0"));
      if (newVersion.compareTo(currentVersion) > 0)
      {
        Logger.info("newer solarreader production version available, current version is {}, newer version is {}", SolarMain.softwareVersion, newVersion);
        return true;
      }
    }

    Logger.info("No update available, current version is {})", SolarMain.softwareVersion);
    return false;

  }

}

