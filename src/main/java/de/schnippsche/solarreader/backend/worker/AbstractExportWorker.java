package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.configuration.ConfigExport;
import de.schnippsche.solarreader.backend.configuration.ConfigMqtt;
import de.schnippsche.solarreader.backend.exporter.Exporter;
import de.schnippsche.solarreader.backend.exporter.InfluxExporter;
import de.schnippsche.solarreader.backend.exporter.MqttExporter;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.DateTimeHelper;
import de.schnippsche.solarreader.backend.utils.JsonTools;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExportWorker implements Runnable
{
  protected final JsonTools jsonTool;
  protected final DateTimeHelper dateTimeHelper;
  private final List<Exporter> exporterList;
  private final Activity activity;

  protected AbstractExportWorker(Activity activity)
  {
    Logger.debug("ExportWorker created with Activity {}", activity);
    this.jsonTool = new JsonTools();
    this.dateTimeHelper = new DateTimeHelper();
    this.exporterList = new ArrayList<>();
    this.activity = activity;
  }

  protected synchronized void addExporter(Exporter exporter)
  {
    Logger.debug("Exporter added {}", exporter);
    this.exporterList.add(exporter);
  }

  protected synchronized void addDatabaseExporter(ConfigExport configExport, List<Table> tables, long startTimestamp)
  {
    if (configExport.getDatabaseList() != null && tables != null)
    {
      for (ConfigDatabase database : configExport.getDatabaseList())
      {
        if (database.isEnabled())
        {
          addExporter(new InfluxExporter(database, tables, startTimestamp));
        }
      }
    }
  }

  protected synchronized void addMqttExporter(ConfigExport configExport, List<ResultField> resultFields, List<MqttField> mqttFields)
  {
    if (configExport.getMqttList() != null && mqttFields != null)
    {
      for (ConfigMqtt configMqtt : configExport.getMqttList())
      {
        if (configMqtt.isEnabled())
        {
          addExporter(new MqttExporter(configMqtt, resultFields, mqttFields, configExport.getMqttTopic().trim()));
        }
      }
    }
  }

  protected void exportAll()
  {
    for (Exporter exporter : this.exporterList)
    {
      exporter.export();
    }
  }

  protected void exportSimple(Table table, ConfigExport configExport, List<ResultField> resultFields, List<MqttField> mqttFields)
  {
    List<Table> tables = new ArrayList<>();
    tables.add(table);
    addDatabaseExporter(configExport, tables, System.currentTimeMillis());
    addMqttExporter(configExport, resultFields, mqttFields);
    exportAll();
  }

  public Activity getActivity()
  {
    return activity;
  }

  @Override public void run()
  {
    exporterList.clear();
    if (activity.mustExecute(LocalDateTime.now()))
    {
      activity.setActive(true);
      activity.setLastCall();
      try
      {
        doWork();
      } catch (Exception e)
      {
        // catch all exceptions to prevent aborting the main thread
        Logger.error(e);
      }
      activity.finish();
    } else
    {
      Logger.debug("outside activity");
    }
  }

  protected abstract void doWork();

}
