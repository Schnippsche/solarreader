package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Activity;

import java.util.List;

public class DeviceWorker extends AbstractExportWorker
{
  private final AbstractDevice device;

  public DeviceWorker(AbstractDevice device)
  {
    super(device.getConfigDevice().getActivity());
    this.device = device;
  }

  @Override protected void doWork()
  {
    getActivity().setActive(true);
    List<Table> tables = device.doWork();
    //
    addDatabaseExporter(device.getConfigDevice().getConfigExport(), tables);
    addMqttExporter(device.getConfigDevice().getConfigExport(), device.getValidResultFields(), device.getSpecification()
                                                                                                     .getMqttFields());
    exportAll();
    tables.clear();
    getActivity().finish();
  }

  @Override public Activity getActivity()
  {
    return device.getActivity();
  }

}
