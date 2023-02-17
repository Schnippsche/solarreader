package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigAwattar;
import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.configuration.ConfigSolarprognose;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.pusher.InfluxPusher;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadHelper
{

  private static ScheduledExecutorService executor;
  private static MainWorker mainWorker;
  private static InfluxPusher influxPusher;
  private static UpdateWorker updateWorker;

  private ThreadHelper()
  {
    // to protect
  }

  public static void startMainThreads()
  {
    Logger.info("start main threads...");
    executor = Executors.newScheduledThreadPool(2);
    mainWorker = new MainWorker();
    influxPusher = new InfluxPusher();
    updateWorker = new UpdateWorker();
    Config.getInstance().getMqttMaster().connectAllMqttClients();
    Config.getInstance().initRules();
    executor.scheduleAtFixedRate(mainWorker, 0, 250, TimeUnit.MILLISECONDS);
  }

  public static void stopThreads()
  {
    Config.getInstance().getMqttMaster().disconnectAllMqttClients();
    if (executor != null)
    {
      shutdown();
    }
    if (mainWorker != null)
    {
      mainWorker.shutdown();
    }
    System.out.println("exit Solarreader");
  }

  private static void shutdown()
  {
    executor.shutdown();
    try
    {
      if (!executor.awaitTermination(1, TimeUnit.SECONDS))
      {
        executor.shutdownNow();
      }
    } catch (Exception e)
    {
      executor.shutdownNow();
    }
  }

  public static List<DeviceWorker> getDeviceWorkers(LocalDateTime time)
  {
    List<DeviceWorker> list = new ArrayList<>();
    for (AbstractDevice device : Config.getInstance().getDevices())
    {
      // device must execute if:
      // normal activity or devicecommands for sending are present!
      List<ExpiringCommand> commands =
        Config.getInstance().getExpiringCommandsForUuid(device.getConfigDevice().getUuid());
      if (device.getActivity().mustExecute(time))
      {
        list.add(new DeviceWorker(device));
      } else if (!commands.isEmpty() && device.getActivity().isEnabled() && !device.getActivity().isActive())
      {
        list.add(new DeviceWorker(device, commands));
      }
    }
    return list;
  }

  public static SolarprognoseWorker getSolarprognoseWorker(LocalDateTime time)
  {
    ConfigSolarprognose configSolarprognose = Config.getInstance().getConfigSolarprognose();
    return configSolarprognose.getActivity().mustExecute(time) ? new SolarprognoseWorker(configSolarprognose) : null;
  }

  public static OpenWeatherWorker getOpenWeatherWorker(LocalDateTime time)
  {
    ConfigOpenWeather configOpenWeather = Config.getInstance().getConfigOpenWeather();
    return configOpenWeather.getActivity().mustExecute(time) ? new OpenWeatherWorker(configOpenWeather) : null;

  }

  public static AwattarWorker getAwattarWorker(LocalDateTime time)
  {
    ConfigAwattar configAwattar = Config.getInstance().getConfigAwattar();
    return configAwattar.getActivity().mustExecute(time) ? new AwattarWorker(configAwattar) : null;
  }

  public static InfluxPusher getInfluxPusher()
  {
    return influxPusher;
  }

  public static UpdateWorker getUpdateWorker(LocalDateTime time)
  {
    return updateWorker.getActivity().mustExecute(time) ? updateWorker : null;
  }

}
