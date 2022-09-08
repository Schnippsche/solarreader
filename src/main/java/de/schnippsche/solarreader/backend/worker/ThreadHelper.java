package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigAwattar;
import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.configuration.ConfigSolarprognose;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.pusher.InfluxPusher;
import de.schnippsche.solarreader.backend.pusher.MqttPusher;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadHelper
{

  private static ScheduledExecutorService executor;
  private static List<DeviceWorker> deviceWorkers;
  private static SolarprognoseWorker solarprognoseWorker;
  private static OpenWeatherWorker openWeatherWorker;
  private static AwattarWorker awattarWorker;
  private static MainWorker mainWorker;
  private static InfluxPusher influxPusher;
  private static MqttPusher mqttPusher;
  private static boolean reloadDeviceConfigurations = true;
  private static boolean reloadSolarprognoseConfiguration = true;
  private static boolean reloadOpenWeatherConfiguration = true;
  private static boolean reloadAwattarConfiguration = true;

  private ThreadHelper()
  {
    // to protect
  }

  public static void startMainThreads()
  {
    executor = Executors.newScheduledThreadPool(2);
    mainWorker = new MainWorker();
    influxPusher = new InfluxPusher();
    mqttPusher = new MqttPusher();
    executor.scheduleAtFixedRate(mainWorker, 0, 1, TimeUnit.SECONDS);
  }

  public static void stopThreads()
  {
    if (executor != null)
    {
      executor.shutdown();
    }
    if (mainWorker != null)
    {
      mainWorker.shutdown();
    }
    try
    {
      if (executor != null && !executor.awaitTermination(5, TimeUnit.SECONDS))
      {
        executor.shutdownNow();
      }
    } catch (InterruptedException e)
    {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  public static List<DeviceWorker> getDeviceWorkers()
  {
    if (reloadDeviceConfigurations)
    {
      deviceWorkers = new ArrayList<>();
      for (AbstractDevice device : Config.getInstance().getDevices())
      {
        deviceWorkers.add(new DeviceWorker(device));
      }
      Logger.debug("getDeviceWorkers with {} devices", deviceWorkers.size());
      reloadDeviceConfigurations = false;
    }
    return deviceWorkers;
  }

  public static SolarprognoseWorker getSolarprognoseWorker()
  {
    if (reloadSolarprognoseConfiguration)
    {
      ConfigSolarprognose configSolarprognose = Config.getInstance().getConfigSolarprognose();
      solarprognoseWorker = new SolarprognoseWorker(configSolarprognose);
      reloadSolarprognoseConfiguration = false;
    }

    return solarprognoseWorker;
  }

  public static OpenWeatherWorker getOpenWeatherWorker()
  {
    if (reloadOpenWeatherConfiguration)
    {
      ConfigOpenWeather configOpenWeather = Config.getInstance().getConfigOpenWeather();
      openWeatherWorker = new OpenWeatherWorker(configOpenWeather);
      reloadOpenWeatherConfiguration = false;
    }
    return openWeatherWorker;
  }

  public static AwattarWorker getAwattarWorker()
  {
    if (reloadAwattarConfiguration)
    {
      ConfigAwattar configAwattar = Config.getInstance().getConfigAwattar();
      awattarWorker = new AwattarWorker(configAwattar);
      reloadAwattarConfiguration = false;
    }
    return awattarWorker;
  }

  public static InfluxPusher getInfluxPusher()
  {
    return influxPusher;
  }

  public static MqttPusher getMqttPusher()
  {
    return mqttPusher;
  }

  public static void changedDeviceConfiguration()
  {
    reloadDeviceConfigurations = true;
  }

  public static void changedSolarprognoseConfiguration()
  {
    reloadSolarprognoseConfiguration = true;
  }

  public static void changedOpenWeatherConfiguration()
  {
    reloadOpenWeatherConfiguration = true;
  }

  public static void changedAwattarConfiguration()
  {
    reloadAwattarConfiguration = true;
  }

}
