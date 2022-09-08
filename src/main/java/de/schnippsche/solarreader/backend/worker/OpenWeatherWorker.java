package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.TableField;
import de.schnippsche.solarreader.backend.serializes.openweather.OpenWeatherWrapper;
import de.schnippsche.solarreader.backend.tables.ExportTables;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.util.List;

public class OpenWeatherWorker extends AbstractExportWorker
{

  private final List<TableField> tableFields;
  private final List<DeviceField> deviceFields;
  private final List<MqttField> mqttFields;
  private final ConfigOpenWeather configOpenWeather;

  public OpenWeatherWorker(ConfigOpenWeather configOpenWeather)
  {
    super(configOpenWeather.getActivity());
    this.configOpenWeather = configOpenWeather;
    Specification specs = jsonTool.readSpecification("openweather");
    this.tableFields = specs.getDatabasefields();
    this.deviceFields = specs.getDevicefields();
    this.mqttFields = specs.getMqttFields();
  }

  @Override protected void doWork()
  {
    Logger.info("Read OpenWeather");
    Logger.debug(configOpenWeather.getApiUrl());
    OpenWeatherWrapper wrapper = jsonTool.getObjectFromUrl(configOpenWeather.getApiUrl(), OpenWeatherWrapper.class);
    if (wrapper == null)
    {
      return;
    }
    Logger.info("OpenWeather successfully read");
    Logger.debug(wrapper);
    List<ResultField> resultFields = wrapper.getResultFields(deviceFields);
    List<Table> tables = new ExportTables().convert(resultFields, tableFields);
    addDatabaseExporter(configOpenWeather.getConfigExport(), tables);
    addMqttExporter(configOpenWeather.getConfigExport(), resultFields, mqttFields);
    exportAll();
    tables.clear();
  }

}
