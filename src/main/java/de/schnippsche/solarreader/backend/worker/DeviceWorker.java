package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.ExpiringCommand;
import org.tinylog.Logger;

import java.util.List;

public class DeviceWorker extends AbstractExportWorker
{
  private final AbstractDevice device;
  private final List<ExpiringCommand> commands;

  public DeviceWorker(AbstractDevice device)
  {
    this(device, null);
  }

  public DeviceWorker(AbstractDevice device, List<ExpiringCommand> commands)
  {
    super(device.getConfigDevice().getActivity());
    this.device = device;
    this.commands = commands;
  }

  @Override public void run()
  {
    getActivity().setActive(true);
    if (commands == null)
    {
      // read the device
      Logger.info("read device");
      getActivity().setLastCall();
      try
      {
        doWork();
      } catch (Exception e)
      {
        // catch all exceptions to prevent aborting the main thread
        Logger.error(e.getMessage());
      }
      getActivity().finish();
      return;
    }
    // send commands to device
    Logger.info("send command to device");
    getActivity().setActive(true);
    try
    {
      device.sendDeviceCommands(commands);
    } catch (Exception e)
    {
      Logger.error(e.getMessage());
    }
    // Remove all Commands
    for (ExpiringCommand command : commands)
    {
      Config.getInstance().removeExpiringCommand(command);
    }
    getActivity().setActive(false);

  }

  @Override protected void doWork()
  {
    List<Table> tables = device.doWork();
    //
    if (tables != null && !tables.isEmpty())
    {
      addDatabaseExporter(device.getConfigDevice().getConfigExport(), tables, device.getStartTimestamp());
      if (device.getSpecification() != null)
      {
        addMqttExporter(device.getConfigDevice()
                              .getConfigExport(), device.getValidResultFields(), device.getSpecification()
                                                                                       .getMqttFields());
      }
      exportAll();
      tables.clear();
    }
  }

  @Override public Activity getActivity()
  {
    return device.getActivity();
  }

  public AbstractDevice getDevice()
  {
    return device;
  }

}
