package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.HashMap;

public class Powmr extends SimpleUsbQmod
{
  private final HashMap<String, Integer> modeMap;
  private final HashMap<Integer, String> statusMap;

  // QPI P30 Protokoll
  public Powmr(ConfigDevice configDevice)
  {
    super(configDevice);
    modeMap = new HashMap<>();
    modeMap.put("P", 1); // Power Mode
    modeMap.put("S", 2); // Standby Mode
    modeMap.put("B", 3); // Battery Mode
    modeMap.put("L", 4); // Line Mode
    modeMap.put("F", 5); // Fault Mode
    modeMap.put("E", 6); // ECO Mode
    modeMap.put("H", 6); // Power Saving Mode
    modeMap.put("Y", 7); // Bypass Mode
    modeMap.put("T", 8); // Battery Test Mode
    modeMap.put("D", 9); // Shutdown Mode
    modeMap.put("G", 10); // Grid Mode
    modeMap.put("C", 11); // Converter/Charge Mode
    modeMap.put("O", 12); // Standby + Charging Mode
    // Warnings
    statusMap = new HashMap<>();
    statusMap.put(0, "Keine Sonne");
    statusMap.put(1, "Wechselrichterfehler");
    statusMap.put(2, "BUS Überspannung");
    statusMap.put(3, "BUS Unterspannung");
    statusMap.put(4, "BUS Fehler");
    statusMap.put(5, "Netzfehler");
    statusMap.put(6, "OPVShort");
    statusMap.put(7, "WR-Spannung zu niedrig");
    statusMap.put(8, "WR-Spannung zu hoch");
    statusMap.put(9, "Temperatur zu hoch");
    statusMap.put(10, "Lüfter blockiert");
    statusMap.put(11, "Batteriespannung zu hoch");
    statusMap.put(12, "Batteriespannung zu niedrig");
    statusMap.put(13, "unbekannt");
    statusMap.put(14, "Battery under shutdown");
    statusMap.put(15, "Batterie derating");
    statusMap.put(16, "Over load");
    statusMap.put(17, "EEPROM Fehler");
    statusMap.put(18, "Inverter over current");
    statusMap.put(19, "Wechselrichter Fehler");
    statusMap.put(20, "Testfehler");
    statusMap.put(21, "OP DC Voltage Over");
    statusMap.put(22, "Batterie nicht angeschlossen");
    statusMap.put(23, "Stromsensor Fehler");
    statusMap.put(24, "Batterie Kurzschluss");
    statusMap.put(25, "Power Limit");
    statusMap.put(26, "PV Strom 1 zu hoch");
    statusMap.put(27, "Mppt 1 überlastet Fehler");
    statusMap.put(28, "MPPT1 überlastet Warnung");
    statusMap.put(29, "Batterie 1 zu schwach zum Laden");
    statusMap.put(30, "PV Strom 2 zu hoch");
    statusMap.put(31, "Mppt 2 überlastet Fehler");
    statusMap.put(32, "MPPT2 überlastet Warnung");
    statusMap.put(33, "Batterie 2 zu schwach zum Laden");
    statusMap.put(34, "PV Strom 3 zu hoch");
    statusMap.put(35, "Mppt 3 überlastet Fehler");
    statusMap.put(36, "MPPT3 überlastet Warnung");
    statusMap.put(37, "Batterie 3 zu schwach zum Laden");
  }

  @Override protected void correctValues()
  {
    ResultField currentValueField = getValidResultField("PV_Ladeleistung");
    if (currentValueField != null)
    {
      BigDecimal factor = dayValueWrapper.getFactor(getConfigDevice().getActivity());
      BigDecimal wattTotal = dayValueWrapper.addValue(currentValueField, factor);
      setWattTotalToday(wattTotal);
      resultFields.add(new ResultField("Tagesenergie", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotal));
    }
    ResultField modus = getValidResultField("Modus");
    if (modus != null)
    {
      String oldValue = String.valueOf(modus.getValue()).trim();
      Integer newValue = modeMap.getOrDefault(oldValue, 0);
      Logger.debug("Modus old value: {}, new value:{}", oldValue, newValue);
      resultFields.add(new ResultField("Device_Status", ResultFieldStatus.VALID, newValue));
    }
    ResultField statusField = getValidResultField("status_all"); /// 38 bytes or more
    BigDecimal errorCode = BigDecimal.ZERO;
    String errorText = "";
    if (statusField != null)
    {
      String statusAll = String.valueOf(statusField.getValue());
      for (int i = 0; i < statusAll.length(); i++)
      {
        if ("1".equals(statusAll.substring(i, i + 1)))
        {
          errorCode = BigDecimal.valueOf(i + 1L);
          errorText = statusMap.getOrDefault(i, "unbekannt");
        }
      }
    }
    resultFields.add(new ResultField("Fehlercode", ResultFieldStatus.VALID, FieldType.NUMBER, errorCode));
    resultFields.add(new ResultField("Fehlermeldung", ResultFieldStatus.VALID, FieldType.STRING, errorText));

  }

}
