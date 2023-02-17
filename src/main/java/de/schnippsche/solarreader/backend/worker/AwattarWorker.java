package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigAwattar;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.awattar.AwattarWrapper;
import de.schnippsche.solarreader.backend.serializes.awattar.Data;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.tables.TableColumn;
import de.schnippsche.solarreader.backend.tables.TableColumnType;
import de.schnippsche.solarreader.backend.tables.TableRow;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AwattarWorker extends AbstractExportWorker
{

  public static final String AWATTAR = "Awattar";
  private final ConfigAwattar configAwattar;
  private final List<DeviceField> deviceFields;
  private final List<MqttField> mqttFields;

  public AwattarWorker(ConfigAwattar configAwattar)
  {
    super(configAwattar.getActivity());
    this.configAwattar = configAwattar;
    Specification specs = jsonTool.readSpecification(AWATTAR.toLowerCase());
    deviceFields = specs.getDevicefields();
    mqttFields = specs.getMqttFields();
  }

  @Override protected void doWork()
  {
    Logger.info("Read Awattar");
    AwattarWrapper wrapper = jsonTool.getObjectFromUrl(configAwattar.getApiUrl(), null, AwattarWrapper.class);
    if (wrapper == null)
    {
      Logger.error("awattar wrapper is null");
      return;
    }
    List<ResultField> resultFields = wrapper.getResultFields(deviceFields);
    // cache valid result fields
    Config.getInstance().setCurrentResultFields(AWATTAR, resultFields);
    Logger.info("Awattar successful, {}", wrapper);
    Table table = new Table("awattarPreise");
    for (int i = 0; i < wrapper.getData().size(); i++)
    {
      TableRow tableRow = new TableRow();
      table.addTableRow(tableRow);
      Data data = wrapper.getData().get(i);
      long timestamp = data.getStartTimestamp() / 1000;
      double marketPrice = data.getMarketprice() / 1000.0 + configAwattar.getPriceCorrection().doubleValue();
      LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
      String stunde = "" + ldt.getHour();
      tableRow.putColumn(new TableColumn("Datum", TableColumnType.STRING, ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
      tableRow.putColumn(new TableColumn("Preis_kWh", TableColumnType.NUMBER, "" + marketPrice));
      tableRow.putColumn(new TableColumn("Stunde", TableColumnType.NUMBER, stunde));
      tableRow.putColumn(new TableColumn("timestamp", TableColumnType.NUMBER, timestamp));
    }
    exportSimple(table, configAwattar.getConfigExport(), resultFields, mqttFields);
  }

}
