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
  private static final String[] statusNames =
    {"Keine Sonne", "Wechselrichterfehler", "BUS Überspannung", "BUS Unterspannung", "BUS Fehler", "Netzfehler", "OPVShort", "WR-Spannung zu niedrig", "WR-Spannung zu hoch", "Temperatur zu hoch", "Lüfter blockiert", "Batteriespannung zu hoch", "Batteriespannung zu niedrig", "unbekannt", "Battery under shutdown", "Batterie derating", "Over load", "EEPROM Fehler", "Inverter over current", "Wechselrichter Fehler", "Testfehler", "OP DC Voltage Over", "Batterie nicht angeschlossen", "Stromsensor Fehler", "Batterie Kurzschluss", "Power Limit", "PV Strom 1 zu hoch", "Mppt 1 überlastet Fehler", "MPPT1 überlastet Warnung", "Batterie 1 zu schwach zum Laden", "PV Strom 2 zu hoch", "Mppt 2 überlastet Fehler", "MPPT2 überlastet Warnung", "Batterie 2 zu schwach zum Laden", "PV Strom 3 zu hoch", "Mppt 3 überlastet Fehler", "MPPT3 überlastet Warnung", "Batterie 3 zu schwach zum Laden"};
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
    for (int i = 0; i < statusNames.length; i++)
    {
      statusMap.put(i, statusNames[i]);
    }
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
