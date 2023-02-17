package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.pusher.InfluxPusher;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainWorker implements Runnable
{

  private final ExecutorService fixedSizeThreadPool;

  public MainWorker()
  {
    int maxThreads = new NumericHelper().getInteger(System.getProperty("solarreader.maxthreads"), 10);
    fixedSizeThreadPool = Executors.newFixedThreadPool(maxThreads);
    Logger.info("MainWorker with max Threads = {}", maxThreads);
  }

  @Override public void run()
  {
    Config.getInstance().getStandardValues().setDateAndTimeValues();
    LocalDateTime time = Config.getInstance().getStandardValues().getLocalDateTime();
    SolarprognoseWorker solarprognoseWorker = ThreadHelper.getSolarprognoseWorker(time);
    OpenWeatherWorker openweatherWorker = ThreadHelper.getOpenWeatherWorker(time);
    AwattarWorker awattarWorker = ThreadHelper.getAwattarWorker(time);
    UpdateWorker updateWorker = ThreadHelper.getUpdateWorker(time);
    InfluxPusher influxPusher = ThreadHelper.getInfluxPusher();
    Config.getInstance().removeExpiredCommands();
    // device configuration
    List<DeviceWorker> deviceWorkers = ThreadHelper.getDeviceWorkers(time);
    Logger.trace("worker running on time {}, deviceWorkers: {} ", time, deviceWorkers.size());
    // process all devices
    if (!deviceWorkers.isEmpty())
    {
      Logger.debug("starting {} device workers...", deviceWorkers.size());
      deviceWorkers.forEach(fixedSizeThreadPool::execute);
    }
    if (solarprognoseWorker != null)
    {
      fixedSizeThreadPool.execute(solarprognoseWorker);
    }

    if (openweatherWorker != null)
    {
      fixedSizeThreadPool.execute(openweatherWorker);
    }

    if (awattarWorker != null)
    {
      fixedSizeThreadPool.execute(awattarWorker);
    }
    if (!influxPusher.isBusy())
    {
      fixedSizeThreadPool.execute(influxPusher);
    }
    if (updateWorker != null)
    {
      fixedSizeThreadPool.execute(updateWorker);
    }
  }

  public void shutdown()
  {
    fixedSizeThreadPool.shutdown();
    try
    {
      if (!fixedSizeThreadPool.awaitTermination(3, TimeUnit.SECONDS))
      {
        fixedSizeThreadPool.shutdownNow();
      }
    } catch (Exception e)
    {
      fixedSizeThreadPool.shutdownNow();
    }
  }

}
