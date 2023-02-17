package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigSolarprognose;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.solarprognose.SolarprognoseWrapper;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.tables.TableColumn;
import de.schnippsche.solarreader.backend.tables.TableColumnType;
import de.schnippsche.solarreader.backend.tables.TableRow;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class SolarprognoseWorker extends AbstractExportWorker
{
  public static final String SOLARPROGNOSE = "Solarprognose";
  private final ConfigSolarprognose configSolarprognose;
  private final List<DeviceField> deviceFields;
  private final List<MqttField> mqttFields;

  public SolarprognoseWorker(ConfigSolarprognose configSolarprognose)
  {
    super(configSolarprognose.getActivity());
    this.configSolarprognose = configSolarprognose;
    Specification specs = jsonTool.readSpecification(SOLARPROGNOSE.toLowerCase());
    deviceFields = specs.getDevicefields();
    mqttFields = specs.getMqttFields();
  }

  @Override protected void doWork()
  {
    Logger.info("Read Solarprognose");
    Logger.debug(configSolarprognose.getApiUrl());
    SolarprognoseWrapper wrapper =
      jsonTool.getObjectFromUrl(configSolarprognose.getApiUrl(), null, SolarprognoseWrapper.class);
    if (wrapper == null)
    {
      Logger.error("solarprognose wrapper is null");
      return;
    }
    Logger.info("Solarprognose query successful");
    // Korrekturen
    boolean sommerzeit = TimeZone.getDefault().inDaylightTime(new Date());
    Logger.debug(sommerzeit ? "Sommerzeit:" : "Winterzeit");
    // test status ; if status = 0 then everything is okay
    if (wrapper.getStatus() != 0)
    {
      Logger.error("solarprognose returns error '{}' , abort parsing.", getErrorCodeText(wrapper.getStatus()));
      return;
    }
    List<ResultField> resultFields = wrapper.getResultFields(deviceFields);
    Config.getInstance().setCurrentResultFields(SOLARPROGNOSE, resultFields);
    final Map<Long, List<Double>> data = wrapper.getData();
    Table table = new Table("Wetterprognose");
    data.forEach((timestamp, detail) ->
    {
      if (detail != null && detail.size() >= 2)
      {
        TableRow tableRow = new TableRow();
        table.addTableRow(tableRow);
        tableRow.putColumn(new TableColumn("timestamp", TableColumnType.NUMBER, timestamp));
        tableRow.putColumn(new TableColumn("Prognose_W", TableColumnType.NUMBER, detail.get(0)));
        tableRow.putColumn(new TableColumn("Prognose_Wh", TableColumnType.NUMBER, detail.get(1)));
        tableRow.putColumn(new TableColumn("datum", TableColumnType.STRING, dateTimeHelper.convertTimestamp(timestamp, "dd.MM.yyyy")));
      }
    });

    exportSimple(table, configSolarprognose.getConfigExport(), resultFields, mqttFields);
  }

  private String getErrorCodeText(int errorCode)
  {
    switch (errorCode)
    {
      case -2:
        return "INVALID_ACCESS_TOKEN";
      case -3:
        return "MISSING_PARAMETER_ACCESS_TOKEN";
      case -4:
        return "EMPTY_PARAMETER_ACCESS_TOKEN";
      case -5:
        return "INVALID_TYPE";
      case -6:
        return "MISSING_TYPE";
      case -7:
        return "INVALID_ID";
      case -8:
        return "ACCESS_DENIED";
      case -9:
        return "INVALID_ITEM";
      case -10:
        return "INVALID_TOKEN";
      case -11:
        return "NO_SOLAR_DATA_AVAILABLE";
      case -12:
        return "NO_DATA";
      case -13:
        return "INTERNAL_ERROR";
      case -14:
        return "UNKNOWN_ERROR";
      case -15:
        return "INVALID_START_DAY";
      case -16:
        return "INVALID_END_DAY";
      case -17:
        return "INVALID_DAY";
      case -18:
        return "INVALID_WEATHER_SERVICE_ID";
      case -19:
        return "DAILY_QUOTA_EXCEEDED";
      case -20:
        return "INVALID_OR_MISSING_ELEMENT_ITEM";
      case -21:
        return "NO_PARAMETER";
      case -22:
        return "INVALID_PERIOD";
      case -23:
        return "INVALID_START_EPOCH_TIME";
      case -24:
        return "INVALID_END_EPOCH_TIME";
      case -25:
        return "ACCESS_DENIED_TO_ITEM_DUE_TO_LIMIT";
      case -26:
        return "NO_CLEARSKY_VALUES ";
      case -27:
        return "MISSING_INPUT_ID_AND_TOKEN";
      case -28:
        return "INVALID_ALGORITHM";
      default:
        return "" + errorCode;
    }
  }

}
