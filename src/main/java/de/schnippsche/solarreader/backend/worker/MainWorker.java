package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.pusher.InfluxPusher;
import de.schnippsche.solarreader.backend.pusher.MqttPusher;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import org.tinylog.Logger;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainWorker implements Runnable
{

  private final ExecutorService fixedSizeThreadPool;

  public MainWorker()
  {

    int maxThreads = new NumericHelper().getInteger(System.getProperty("solarreader.maxthreads"), 8);
    fixedSizeThreadPool = Executors.newFixedThreadPool(maxThreads);
    Logger.info("MainWorker with max Threads = {}", maxThreads);
  }

  @Override public void run()
  {
    // device configuration changed?
    Config.getInstance().checkDeviceConfigurationForUpdates();
    SolarprognoseWorker solarprognoseWorker = ThreadHelper.getSolarprognoseWorker();
    OpenWeatherWorker openweatherWorker = ThreadHelper.getOpenWeatherWorker();
    AwattarWorker awattarWorker = ThreadHelper.getAwattarWorker();
    InfluxPusher influxPusher = ThreadHelper.getInfluxPusher();
    MqttPusher mqttPusher = ThreadHelper.getMqttPusher();
    List<DeviceWorker> deviceWorkers = ThreadHelper.getDeviceWorkers();
    Config.getInstance().getStandardValues().setDateAndTimeValues();
    LocalTime time = Config.getInstance().getStandardValues().getLocalTime();
    Logger.trace("worker running on time {}, deviceWorkers: {} ", time, deviceWorkers.size());
    // process all devices
    if (!deviceWorkers.isEmpty())
    {
      deviceWorkers.stream()
                   .filter(deviceWorker -> deviceWorker.getActivity().mustExecute(time))
                   .forEach(fixedSizeThreadPool::execute);
    }
    if (solarprognoseWorker != null && solarprognoseWorker.getActivity().mustExecute(time))
    {
      fixedSizeThreadPool.execute(solarprognoseWorker);
    }

    if (openweatherWorker != null && openweatherWorker.getActivity().mustExecute(time))
    {
      fixedSizeThreadPool.execute(openweatherWorker);
    }

    if (awattarWorker != null && awattarWorker.getActivity().mustExecute(time))
    {
      fixedSizeThreadPool.execute(awattarWorker);
    }
    if (!mqttPusher.isBusy())
    {
      fixedSizeThreadPool.execute(mqttPusher);
    }
    if (!influxPusher.isBusy())
    {
      fixedSizeThreadPool.execute(influxPusher);
    }
  }

  public void shutdown()
  {
    fixedSizeThreadPool.shutdown();
    try
    {
      if (!fixedSizeThreadPool.awaitTermination(5, TimeUnit.SECONDS))
      {
        fixedSizeThreadPool.shutdownNow();
      }
    } catch (InterruptedException e)
    {
      fixedSizeThreadPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

}
