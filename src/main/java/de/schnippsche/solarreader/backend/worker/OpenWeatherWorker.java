package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.fields.*;
import de.schnippsche.solarreader.backend.tables.ExportTables;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class OpenWeatherWorker extends AbstractExportWorker
{

  private final List<TableField> tableFields;
  private final List<MqttField> mqttFields;
  private final ConfigOpenWeather configOpenWeather;

  public OpenWeatherWorker(ConfigOpenWeather configOpenWeather)
  {
    super(configOpenWeather.getActivity());
    this.configOpenWeather = configOpenWeather;
    Specification specs = jsonTool.readSpecification("openweather");
    this.tableFields = specs.getDatabasefields();
    this.mqttFields = specs.getMqttFields();
  }

  @Override protected void doWork()
  {
    Logger.info("Read OpenWeather");
    Logger.debug(configOpenWeather.getApiUrl());
    List<ResultField> resultFields = jsonTool.getResultFieldsFromUrl(configOpenWeather.getApiUrl());
    if (resultFields == null || resultFields.isEmpty())
    {
      return;
    }
    Logger.info("OpenWeather successfully read");
    // Correct rain
    BigDecimal rain = BigDecimal.ZERO;
    Optional<ResultField> rain3h = resultFields.stream().filter(rf -> rf.getName().equals("rain_3h")).findFirst();
    if (rain3h.isPresent())
    {
      rain = rain3h.get().getNumericValue();
      Logger.debug("rain 3h present");
    } else
    {
      Optional<ResultField> rain1h = resultFields.stream().filter(rf -> rf.getName().equals("rain_1h")).findFirst();
      if (rain1h.isPresent())
      {
        rain = rain1h.get().getNumericValue();
        Logger.debug("rain 1h present");
      }
    }
    resultFields.add(new ResultField("rain", ResultFieldStatus.VALID, FieldType.NUMBER, rain));
    // correct snow
    BigDecimal snow = BigDecimal.ZERO;
    Optional<ResultField> snow3h = resultFields.stream().filter(rf -> rf.getName().equals("snow_3h")).findFirst();
    if (snow3h.isPresent())
    {
      snow = snow3h.get().getNumericValue();
      Logger.debug("snow 3h present");
    } else
    {
      Optional<ResultField> snow1h = resultFields.stream().filter(rf -> rf.getName().equals("snow_1h")).findFirst();
      if (snow1h.isPresent())
      {
        snow = snow1h.get().getNumericValue();
        Logger.debug("snow 1h present");
      }
    }
    resultFields.add(new ResultField("snow", ResultFieldStatus.VALID, FieldType.NUMBER, snow));
    // sunrise and sunset calculate from timestamp to datetime
    Optional<ResultField> sunrise = resultFields.stream().filter(rf -> rf.getName().equals("sys_sunrise")).findFirst();
    if (sunrise.isPresent())
    {
      long ts = sunrise.get().getNumericValue().longValue();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
      LocalDateTime utcDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneOffset.UTC);
      LocalDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
      resultFields.add(new ResultField("sunrise_time", ResultFieldStatus.VALID, FieldType.STRING, localDateTime.format(formatter)));
    }
    Optional<ResultField> sunset = resultFields.stream().filter(rf -> rf.getName().equals("sys_sunset")).findFirst();
    if (sunset.isPresent())
    {
      long ts = sunset.get().getNumericValue().longValue();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
      LocalDateTime utcDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneOffset.UTC);
      LocalDateTime localDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
      resultFields.add(new ResultField("sunset_time", ResultFieldStatus.VALID, FieldType.STRING, localDateTime.format(formatter)));
    }
    //

    List<Table> tables = new ExportTables().convert(resultFields, tableFields);
    addDatabaseExporter(configOpenWeather.getConfigExport(), tables);
    addMqttExporter(configOpenWeather.getConfigExport(), resultFields, mqttFields);
    exportAll();
    tables.clear();
  }

}
