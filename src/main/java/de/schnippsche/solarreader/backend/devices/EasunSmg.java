package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import de.schnippsche.solarreader.backend.utils.WattPerDay;

import java.math.BigDecimal;
import java.util.List;

public class EasunSmg extends AbstractLockedDevice
{
  private final String model;
  private ModbusWrapper modbusWrapper;
  private WattPerDay wattPerDay;

  public EasunSmg(ConfigDevice configDevice)
  {
    super(configDevice);
    model = "Easun SMG";
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    specification = jsonTool.readSpecification("easun_smg");
    wattPerDay = new WattPerDay(getConfigDevice());
  }

  // 71 = EASUN SMG II Wechselrichter, BAUDRATE 9600
  @Override protected boolean readLockedDeviceValues()
  {
    List<ResultField> readResultFields = modbusWrapper.readAllFields(specification.getDevicefields());
    resultFields.addAll(readResultFields);
    return !readResultFields.isEmpty();
  }

  @Override protected void correctValues()
  {
    resultFields.add(new ResultField("modell", ResultFieldStatus.VALID, FieldType.STRING, model));
    BigDecimal wattTotal = wattPerDay.addWattHours(getValidResultField("PV_Leistung"));
    resultFields.add(new ResultField("WattStundenGesamtHeute", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotal));
  }

  @Override protected void createTables()
  {
    this.tables.addAll(exportTables.convert(resultFields, specification.getDatabasefields()));
  }

}
