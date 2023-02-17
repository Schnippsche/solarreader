package de.schnippsche.solarreader.backend.worker;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigOpenWeather;
import de.schnippsche.solarreader.backend.fields.*;
import de.schnippsche.solarreader.backend.tables.ExportTables;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OpenWeatherWorker extends AbstractExportWorker
{

  public static final String OPENWEATHER = "OpenWeather";
  private final List<TableField> tableFields;
  private final List<MqttField> mqttFields;
  private final ConfigOpenWeather configOpenWeather;
  private final DateTimeFormatter formatter;

  public OpenWeatherWorker(ConfigOpenWeather configOpenWeather)
  {
    super(configOpenWeather.getActivity());
    this.configOpenWeather = configOpenWeather;
    Specification specs = jsonTool.readSpecification(OPENWEATHER.toLowerCase());
    this.tableFields = specs.getDatabasefields();
    this.mqttFields = specs.getMqttFields();
    formatter = DateTimeFormatter.ofPattern("HH:mm");
  }

  @Override protected void doWork()
  {
    Logger.info("Read OpenWeather");
    Logger.debug(configOpenWeather.getApiUrl());
    List<ResultField> resultFields = jsonTool.getResultFieldsFromUrl(configOpenWeather.getApiUrl(), null);
    if (resultFields == null || resultFields.isEmpty())
    {
      return;
    }
    Logger.info("OpenWeather successfully read");
    // correct rain
    ResultField rain1hField = getResultField("rain_1h", resultFields);
    ResultField rain3hField = getResultField("rain_3h", resultFields);
    BigDecimal rain = rain3hField != null ? rain3hField.getNumericValue() : BigDecimal.ZERO;
    rain = rain1hField != null ? rain1hField.getNumericValue() : rain;
    resultFields.add(new ResultField("rain", ResultFieldStatus.VALID, FieldType.NUMBER, rain));
    //
    // correct snow
    ResultField snow1hField = getResultField("snow_1h", resultFields);
    ResultField snow3hField = getResultField("snow_3h", resultFields);
    BigDecimal snow = snow3hField != null ? snow3hField.getNumericValue() : BigDecimal.ZERO;
    snow = snow1hField != null ? snow1hField.getNumericValue() : snow;
    resultFields.add(new ResultField("snow", ResultFieldStatus.VALID, FieldType.NUMBER, snow));
    //
    // timeshift
    ResultField timezoneField = getResultField("timezone", resultFields);
    BigDecimal timeshift = (timezoneField != null) ? timezoneField.getNumericValue() : BigDecimal.ZERO;
    long timeshiftSeconds = timeshift.longValue();
    // sunrise and sunset calculate from timestamp to datetime
    ResultField sunriseField = getResultField("sys_sunrise", resultFields);
    if (sunriseField != null)
    {
      BigDecimal sunrise = sunriseField.getNumericValue();
      long ts = sunrise.longValue() + timeshiftSeconds;
      LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneOffset.UTC);
      Logger.debug("sunrise ts={}, localdateTime={}", ts, localDateTime);
      resultFields.add(new ResultField("sunrise_time", ResultFieldStatus.VALID, FieldType.STRING, localDateTime.format(formatter)));
    } else
    {
      Logger.debug("sunrise is null");
    }
    ResultField sunsetField = getResultField("sys_sunset", resultFields);
    if (sunsetField != null)
    {
      BigDecimal sunset = sunsetField.getNumericValue();
      long ts = sunset.longValue() + timeshiftSeconds;
      LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(ts), ZoneOffset.UTC);
      resultFields.add(new ResultField("sunset_time", ResultFieldStatus.VALID, FieldType.STRING, localDateTime.format(formatter)));
    }
    Config.getInstance().setCurrentResultFields(OPENWEATHER, resultFields);
    //
    List<Table> tables = new ExportTables().convert(resultFields, tableFields);
    addDatabaseExporter(configOpenWeather.getConfigExport(), tables, System.currentTimeMillis());
    addMqttExporter(configOpenWeather.getConfigExport(), resultFields, mqttFields);
    exportAll();
    tables.clear();
  }

  protected ResultField getResultField(String fieldname, List<ResultField> resultFields)
  {
    for (ResultField f : resultFields)
    {
      if (f.isName(fieldname))
      {
        return f;
      }
    }
    return null;
  }

}
