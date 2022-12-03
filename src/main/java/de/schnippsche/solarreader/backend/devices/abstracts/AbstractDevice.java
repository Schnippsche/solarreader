package de.schnippsche.solarreader.backend.devices.abstracts;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.exporter.Exporter;
import de.schnippsche.solarreader.backend.fields.*;
import de.schnippsche.solarreader.backend.tables.ExportTables;
import de.schnippsche.solarreader.backend.tables.StatistikTable;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.utils.Activity;
import de.schnippsche.solarreader.backend.utils.JsonTools;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public abstract class AbstractDevice
{
  protected final List<Exporter> exporter;
  protected final List<ResultField> resultFields;
  protected final List<MqttField> mqttFields;
  protected final List<Table> tables;
  protected final JsonTools jsonTool;
  protected final NumericHelper numericHelper;
  protected final ExportTables exportTables;
  protected final String deviceDescription;
  private final ConfigDevice configDevice;
  protected Specification specification;
  private boolean initializeState;
  private LocalDateTime lastStatisticTableCall;
  private BigDecimal wattTotalToday;

  /**
   * Abstract super class for all devices
   *
   * @param configDevice the configuration for the device
   */
  protected AbstractDevice(ConfigDevice configDevice)
  {
    this.configDevice = configDevice;
    initializeState = true;
    jsonTool = new JsonTools();
    resultFields = new ArrayList<>();
    mqttFields = new ArrayList<>();
    tables = new ArrayList<>();
    exporter = new ArrayList<>();
    numericHelper = new NumericHelper();
    exportTables = new ExportTables();
    deviceDescription = configDevice.getDescription();
    lastStatisticTableCall = null;
    wattTotalToday = BigDecimal.ZERO;
  }

  /**
   * method for initialize things, called only once after start
   */
  protected abstract void initialize();

  /**
   * method for reading the device values
   *
   * @return true if okay or false if error occurs
   */
  protected abstract boolean readDeviceValues();

  /**
   * this method is called after reading deivce values to fix or add some Resultfield values
   * override it for special handling
   */
  protected void correctValues()
  {
    // can be overwritten
  }

  /**
   * this method add all Resultfields to List of Tables
   * overwrite it for special handling
   */
  protected void createTables()
  {
    tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

  /**
   * get the current Specification of the device
   *
   * @return the current specification
   */
  public Specification getSpecification()
  {
    return specification;
  }

  /**
   * set the Specification of the device
   *
   * @param specification the new Specification
   */
  public void setSpecification(Specification specification)
  {
    this.specification = specification;
  }

  /**
   * method for doing the whole thing
   *
   * @return List of Tables for export
   */
  public List<Table> doWork()
  {
    long ms = System.currentTimeMillis();
    Logger.info("read device {}", deviceDescription);
    this.tables.clear();
    this.resultFields.clear();
    this.exporter.clear();
    try
    {
      if (initializeState)
      {
        initialize();
        initializeState = false;
      }
      if (!readDeviceValues())
      {
        Logger.error("device {} could not be read", deviceDescription);
        return this.tables;
      }
      Logger.debug("device read in {} ms", System.currentTimeMillis() - ms);
      // okay
      Logger.info("device {} values: {}", deviceDescription, this.resultFields);
      // Standardvalues
      resultFields.add(new ResultField("objekt", ResultFieldStatus.VALID, FieldType.STRING, configDevice.getDeviceName()));
      correctValues();
      Logger.debug("Device {}, corrected values: {}", deviceDescription, this.resultFields);
      Logger.info("Device {}, createTables...", deviceDescription);
      createTables();
      // Create statistic table only when last call was 10 minutes ago
      int statisticInterval = Config.getInstance().getConfigGeneral().getStatisticInterval();
      if (lastStatisticTableCall == null || LocalDateTime.now()
                                                         .minus(statisticInterval, ChronoUnit.MINUTES)
                                                         .isAfter(lastStatisticTableCall))
      {
        lastStatisticTableCall = LocalDateTime.now();
        StatistikTable statistikTable = new StatistikTable();
        statistikTable.setWattTotalToday(wattTotalToday);
        tables.addAll(statistikTable.createTable());
      }
      StringJoiner joiner = new StringJoiner(",");
      for (Table table : tables)
      {

        String tableName = table.getTableName();
        joiner.add(tableName);
      }
      String allTableNames = joiner.toString();
      Logger.info("Device {}, return tables {}", deviceDescription, allTableNames);
      Logger.debug("Device {} work finished in {} ms", deviceDescription, System.currentTimeMillis() - ms);
    } catch (Exception e)
    {
      // catch all exceptions to prevent aborting the main thread
      Logger.error(e);
    }
    return this.tables;
  }

  /**
   * set the new initilaized state
   *
   * @param state the new state
   */
  public void setInitializeState(boolean state)
  {
    initializeState = state;
  }

  public void setWattTotalToday(BigDecimal wattTotalToday)
  {
    this.wattTotalToday = wattTotalToday;
  }

  public void setWattTotalResultField(String resultFieldName)
  {
    ResultField wattPerDayField = getValidResultField(resultFieldName);
    if (wattPerDayField != null)
    {
      wattTotalToday = wattPerDayField.getNumericValue();
    }
  }

  public List<Table> getTables()
  {
    return tables;
  }

  public ConfigDevice getConfigDevice()
  {
    return configDevice;
  }

  public Activity getActivity()
  {
    return configDevice.getActivity();
  }

  /**
   * get a list of all valid result fields
   *
   * @return List of ResultFields
   */
  public List<ResultField> getValidResultFields()
  {
    List<ResultField> list = new ArrayList<>();
    for (ResultField resultField : resultFields)
    {
      if (resultField.isValid())
      {
        list.add(resultField);
      }
    }
    return list;
  }

  /**
   * search all valid device fields and return the DeviceField with a specific fieldname
   *
   * @param fieldname the fieldname of the device field
   * @return the valid DeviceField with the fieldname or null if nothing found
   */
  protected ResultField getValidResultField(String fieldname)
  {
    for (ResultField f : resultFields)
    {
      if (f.isValid() && f.isName(fieldname))
      {
        return f;
      }
    }
    return null;
  }

  /**
   * search all device fields and return the DeviceField with a specific fieldname
   *
   * @param fieldname the fieldname of the device field
   * @return the DeviceField with the fieldname or null if nothing found
   */
  protected DeviceField getDeviceField(String fieldname)
  {
    if (specification == null)
    {
      return null;
    }
    for (DeviceField f : specification.getDevicefields())
    {
      if (fieldname.equals(f.getName()))
      {
        return f;
      }
    }
    return null;
  }

}
