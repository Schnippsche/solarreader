package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FroniusApi extends AbstractDevice
{
  private static final String[] modelNames =
    {"Fronius Primo 15.0-1 208-240", "Fronius Primo 12.5-1 208-240", "Fronius Primo 11.4-1 208-240", "Fronius Primo 10.0-1 208-240", "Fronius Symo 15.0-3 208", "Fronius Eco 27.0-3-S", "Fronius Eco 25.0-3-S", "Fronius Primo 6.0-1", "Fronius Primo 5.0-1", "Fronius Primo 4.6-1", "Fronius Primo 4.0-1", "Fronius Primo 3.6-1", "Fronius Primo 3.5-1", "Fronius Primo 3.0-1", "Fronius Symo Hybrid 4.0-3-S", "Fronius Symo Hybrid 3.0-3-S", "Fronius IG Plus 120 V-1", "Fronius Primo 3.8-1 208-240", "Fronius Primo 5.0-1 208-240", "Fronius Primo 6.0-1 208-240", "Fronius Primo 7.6-1 208-240", "Fronius Symo 24.0-3 USA Dummy", "Fronius Symo 24.0-3 480", "Fronius Symo 22.7-3 480", "Fronius Symo 20.0-3 480", "Fronius Symo 17.5-3 480", "Fronius Symo 15.0-3 480", "Fronius Symo 12.5-3 480", "Fronius Symo 10.0-3 480", "Fronius Symo 12.0-3 208-240", "Fronius Symo 10.0-3 208-240", "Fronius Symo Hybrid 5.0-3-S", "Fronius Primo 8.2-1 Dummy", "Fronius Primo 8.2-1 208-240", "Fronius Primo 8.2-1", "Fronius Agilo TL 360.0-3", "Fronius Agilo TL 460.0-3", "Fronius Symo 7.0-3-M", "Fronius Galvo 3.1-1 208-240", "Fronius Galvo 2.5-1 208-240", "Fronius Galvo 2.0-1 208-240", "Fronius Galvo 1.5-1 208-240", "Fronius Symo 6.0-3-M", "Fronius Symo 4.5-3-M", "Fronius Symo 3.7-3-M", "Fronius Symo 3.0-3-M", "Fronius Symo 17.5-3-M", "Fronius Symo 15.0-3-M", "Fronius Agilo 75.0-3 Outdoor", "Fronius Agilo 100.0-3 Outdoor", "Fronius IG Plus 55 V-1", "Fronius IG Plus 55 V-2", "Fronius Symo 20.0-3 Dummy", "Fronius Symo 20.0-3-M", "Fronius Symo 5.0-3-M", "Fronius Symo 8.2-3-M", "Fronius Symo 6.7-3-M", "Fronius Symo 5.5-3-M", "Fronius Symo 4.5-3-S", "Fronius Symo 3.7-3-S", "Fronius IG Plus 60 V-2", "Fronius IG Plus 60 V-1", "SPR 8001F-3 EU", "Fronius IG Plus 25 V-1", "Fronius IG Plus 100 V-3", "Fronius Agilo 100.0-3", "SPR 3001F-1 EU", "Fronius IG Plus V/A 10.0-3 Delta", "Fronius IG 50", "Fronius IG Plus 30 V-1", "SPR-11401f-1 UNI", "SPR-12001f-3 WYE277", "SPR-11401f-3 Delta", "SPR-10001f-1 UNI", "SPR-7501f-1 UNI", "SPR-6501f-1 UNI", "SPR-3801f-1 UNI", "SPR-3301f-1 UNI", "SPR 12001F-3 EU", "SPR 10001F-3 EU", "SPR 8001F-2 EU", "SPR 6501F-2 EU", "SPR 4001F-1 EU", "SPR 3501F-1 EU", "Fronius CL 60.0 WYE277 Dummy", "Fronius CL 55.5 Delta Dummy", "Fronius CL 60.0 Dummy", "Fronius IG Plus V 12.0-3 Dummy", "Fronius IG Plus V 7.5-1 Dummy", "Fronius IG Plus V 3.8-1 Dummy", "Fronius IG Plus 150 V-3 Dummy", "Fronius IG Plus 100 V-2 Dummy", "Fronius IG Plus 50 V-1 Dummy", "Fronius IG Plus V/A 12.0-3 WYE", "Fronius IG Plus V/A 11.4-3 Delta", "Fronius IG Plus V/A 11.4-1 UNI", "Fronius IG Plus V/A 10.0-1 UNI", "Fronius IG Plus V/A 7.5-1 UNI", "Fronius IG Plus V/A 6.0-1 UNI", "Fronius IG Plus V/A 5.0-1 UNI", "Fronius IG Plus V/A 3.8-1 UNI", "Fronius IG Plus V/A 3.0-1 UNI", "Fronius IG Plus 150 V-3", "Fronius IG Plus 120 V-3", "Fronius IG Plus 100 V-2", "Fronius IG Plus 100 V-1", "Fronius IG Plus 70 V-2", "Fronius IG Plus 70 V-1", "Fronius IG Plus 50 V-1", "Fronius IG Plus 35 V-1", "SPR 11400f-3 208/240", "SPR 12000f-277", "SPR 10000f", "SPR 10000F EU", "Fronius CL 33.3 Delta", "Fronius CL 44.4 Delta", "Fronius CL 55.5 Delta", "Fronius CL 36.0 WYE277", "Fronius CL 48.0 WYE277", "Fronius CL 60.0 WYE277", "Fronius CL 36.0", "Fronius CL 48.0", "Fronius IG TL 3.0", "Fronius IG TL 4.0", "Fronius IG TL 5.0", "Fronius IG TL 3.6", "Fronius IG TL Dummy", "Fronius IG TL 4.6", "SPR 12000F EU", "SPR 8000F EU", "SPR 6500F EU", "SPR 4000F EU", "SPR 3300F EU", "Fronius CL 60.0", "SPR 12000f", "SPR 8000f", "SPR 6500f", "SPR 4000f", "SPR 3300f", "Fronius IG Plus 12.0-3 WYE277", "Fronius IG Plus 50", "Fronius IG Plus 100", "Fronius IG Plus 100", "Fronius IG Plus 150", "Fronius IG Plus 35", "Fronius IG Plus 70", "Fronius IG Plus 70", "Fronius IG Plus 120", "Fronius IG Plus 3.0-1 UNI", "Fronius IG Plus 3.8-1 UNI", "Fronius IG Plus 5.0-1 UNI", "Fronius IG Plus 6.0-1 UNI", "Fronius IG Plus 7.5-1 UNI", "Fronius IG Plus 10.0-1 UNI", "Fronius IG Plus 11.4-1 UNI", "Fronius IG Plus 11.4-3 Delta", "Fronius Galvo 3.0-1", "Fronius Galvo 2.5-1", "Fronius Galvo 2.0-1", "Fronius IG 4500-LV", "Fronius Galvo 1.5-1", "Fronius IG 2500-LV", "Fronius Agilo 75.0-3", "Fronius Agilo 100.0-3 Dummy", "Fronius Symo 10.0-3-M", "Fronius Symo 12.5-3-M", "Fronius IG 5100", "Fronius IG 4000", "Fronius Symo 8.2-3-M Dummy", "Fronius IG 3000", "Fronius IG 2000", "Fronius Galvo 3.1-1 Dummy", "Fronius IG Plus 80 V-3", "Fronius IG Plus 60 V-3", "Fronius IG Plus 55 V-3", "Fronius IG 60 ADV", "Fronius IG 500", "Fronius IG 400", "Fronius IG 300", "Fronius Symo 3.0-3-S", "Fronius Galvo 3.1-1", "Fronius IG 60 HV", "Fronius IG 40", "Fronius IG 30 Dummy", "Fronius IG 30", "Fronius IG 20", "Fronius IG 15"};
  private static final String BODY_DATA = "Body_Data_";
  protected final DayValueWrapper dayValueWrapper;
  private final DateTimeFormatter dateTimeFormatter;
  private final Map<Integer, String> modelMap;
  private final Pattern pattern;
  private String url;
  private String mainUrl;
  private String baseUrl;
  private String compatibility;
  private boolean gen24;
  private String deviceId;

  public FroniusApi(ConfigDevice configDevice)
  {
    super(configDevice);
    pattern = Pattern.compile("(.*)_Values_(\\d+)");
    dayValueWrapper = new DayValueWrapper(configDevice);
    modelMap = new HashMap<>();
    modelMap.put(1, "Fronius Symo Gen24");
    for (int i = 0; i < modelNames.length; i++)
    {
      modelMap.put(67 + i, modelNames[i]);
    }
    dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    mainUrl =
      String.format("http://%s:%s", getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_IP, "localhost"), getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_PORT, "80"));
    deviceId = getConfigDevice().getParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, "1");
    Logger.debug("mainUrl = {}, device id = {}", mainUrl, deviceId);
  }

  @Override protected boolean readDeviceValues()
  {
    if (!checkApi())
    {
      return false;
    }

    if ("1.6-3".equals(compatibility) || "1.5-18".equals(compatibility) || "1.5-13".equals(compatibility)
        || "1.8-1".equals(compatibility))
    {
      getArchiveData();
      gen24 = false;
    } else
    {
      gen24 = true;
      Logger.debug("model is Fronius Gen 24");
    }

    getInverterRealTimeData();
    getInverterInfo();
    getActiveDeviceInfo();
    getPowerFlowRealtimeData();
    getMeterRealtimeData();
    getOhmPilotRealtimeData();
    getStorageRealtimeData();
    getStringRealtimeData();
    return true;
  }

  @Override public boolean checkConnection()
  {
    initialize();
    return checkApi();
  }

  private boolean checkApi()
  {
    Logger.debug("getInverterInfo");
    url = mainUrl + "/solar_api/GetAPIVersion.cgi";
    Map<String, Object> map = jsonTool.getSimpleMapFromUrl(url, null);
    Object apiVersion = map.getOrDefault("APIVersion", null);
    Logger.debug("apiVersion = {}", apiVersion);
    if (numericHelper.getInteger(apiVersion, 0) == 1)
    {
      baseUrl = mainUrl + map.getOrDefault("BaseURL", "/solar_api/v1/");
      compatibility = String.valueOf(map.get("CompatibilityRange"));
      Logger.debug("Fronius API version {} detected!", apiVersion);
      Logger.debug("baseUrl = {}, compatibilityRange = {}", baseUrl, compatibility);
      return true;
    } else
    {
      Logger.error("no valid api version '{}', must be 1", apiVersion);
    }
    return false;
  }

  private void getArchiveData()
  {
    Logger.debug("getArchiveData");
    ZonedDateTime zdt = ZonedDateTime.now();
    String startDate = zdt.minusSeconds(400).format(dateTimeFormatter);
    String endDate = zdt.format(dateTimeFormatter);
    String first = "inverter/" + deviceId;
    url =
      String.format("%sGetArchiveData.cgi?Scope=System&StartDate=%s&EndDate=%s&Channel=Voltage_DC_String_1&Channel=Voltage_DC_String_2&Channel=Current_DC_String_1&Channel=Current_DC_String_2&Channel=Temperature_Powerstage", baseUrl, startDate, endDate);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    BigDecimal spannung = numericHelper.getBigDecimal(map.get(first + "_Data_Voltage_DC_String_1"));
    BigDecimal strom = numericHelper.getBigDecimal(map.get(first + "_Data_Current_DC_String_1"));
    if (spannung != null && strom != null)
    {
      resultFields.add(new ResultField("Solarspannung_String_1", spannung));
      resultFields.add(new ResultField("Solarstrom_String_1", strom));
      resultFields.add(new ResultField("Solarleistung_String_1", spannung.multiply(strom)));
    }
    spannung = numericHelper.getBigDecimal(map.get(first + "_Data_Voltage_DC_String_2"));
    strom = numericHelper.getBigDecimal(map.get(first + "_Data_Current_DC_String_2"));
    if (spannung != null && strom != null)
    {
      resultFields.add(new ResultField("Solarspannung_String_2", spannung));
      resultFields.add(new ResultField("Solarstrom_String_2", strom));
      resultFields.add(new ResultField("Solarleistung_String_2", spannung.multiply(strom)));
    }
    resultFields.add(new ResultField("Temperatur", map.get(first + "_Data_Temperature_Powerstage")));
  }

  private void getInverterRealTimeData()
  {
    Logger.debug("getInverterRealTimeData");
    url =
      String.format("%sGetInverterRealtimeData.cgi?Scope=Device&DataCollection=CumulationInverterData&DeviceId=%s", baseUrl, deviceId);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    resultFields.add(new ResultField("Geraetestatus", map.getOrDefault("DeviceStatus_StatusCode", BigDecimal.ZERO)));
    resultFields.add(new ResultField("WattstundenGesamtHeute", map.getOrDefault("DAY_ENERGY_Value", BigDecimal.ZERO)));
    resultFields.add(new ResultField("WattstundenGesamtJahr", map.getOrDefault("YEAR_ENERGY_Value", BigDecimal.ZERO)));
    resultFields.add(new ResultField("WattstundenGesamt", map.getOrDefault("TOTAL_ENERGY_Value", BigDecimal.ZERO)));
    resultFields.add(new ResultField("ErrorCodes", map.getOrDefault("DeviceStatus_ErrorCode", BigDecimal.ZERO)));
    resultFields.add(new ResultField("AC_Wirkleistung", map.getOrDefault("PAC_Value", BigDecimal.ZERO)));
    url =
      String.format("%sGetInverterRealtimeData.cgi?Scope=Device&DataCollection=CommonInverterData&DeviceId=%s", baseUrl, deviceId);
    map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    if (map.containsKey("FAC_Value"))
    {
      resultFields.add(new ResultField("AC_Ausgangsfrequenz", map.get("FAC_Value")));
      resultFields.add(new ResultField("AC_Wirkleistung", map.get("PAC_Value")));
      resultFields.add(new ResultField("AC_Ausgangsstrom", map.get("IAC_Value")));
      resultFields.add(new ResultField("AC_Ausgangsspannung", map.get("UAC_Value")));
      resultFields.add(new ResultField("Solarstrom", map.get("IDC_Value")));
      resultFields.add(new ResultField("Solarspannung", map.get("UDC_Value")));
      if (gen24)
      {
        BigDecimal spannung;
        BigDecimal strom;
        int idx;
        if (map.containsKey("UDC_Value"))
        {
          spannung = numericHelper.getBigDecimal(map.get("UDC_Value"));
          strom = numericHelper.getBigDecimal(map.get("IDC_Value"));
          idx = 1;
        } else if (map.containsKey("UDC_1_Value"))
        {
          spannung = numericHelper.getBigDecimal(map.get("UDC_1_Value"));
          strom = numericHelper.getBigDecimal(map.get("IDC_1_Value"));
          idx = 1;
        } else if (map.containsKey("UDC_2_Value"))
        {
          spannung = numericHelper.getBigDecimal(map.get("UDC_2_Value"));
          strom = numericHelper.getBigDecimal(map.get("IDC_2_Value"));
          idx = 2;
        } else if (map.containsKey("UDC_3_Value"))
        {
          spannung = numericHelper.getBigDecimal(map.get("UDC_3_Value"));
          strom = numericHelper.getBigDecimal(map.get("IDC_3_Value"));
          idx = 3;
        } else
        {
          spannung = BigDecimal.ZERO;
          strom = BigDecimal.ZERO;
          idx = 1;
        }
        resultFields.add(new ResultField("Solarspannung_String_" + idx, spannung));
        resultFields.add(new ResultField("Solarstrom_String_" + idx, strom));
        resultFields.add(new ResultField("Solarleistung_String_" + idx, spannung.multiply(strom)));

      }
    }
    if (!gen24)
    {
      resultFields.add(new ResultField("Geraetestatus", map.get("DeviceStatus_StatusCode")));
      resultFields.add(new ResultField("ErrorCodes", map.get("ErrorCode")));
    }

  }

  private void getInverterInfo()
  {
    Logger.debug("getInverterInfo");
    url = String.format("%sGetInverterInfo.cgi", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    resultFields.add(new ResultField("ModulPVLeistung", map.get(deviceId + "_PVPower")));
    resultFields.add(new ResultField("Gen24Status", map.get(deviceId + "_StatusCode")));
  }

  private void getActiveDeviceInfo()
  {
    Logger.debug("getActiveDeviceInfo");
    url = String.format("%sGetActiveDeviceInfo.cgi?DeviceClass=System", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    if (map.containsKey("Ohmpilot"))
    {
      resultFields.add(new ResultField("Ohmpilot", count("Ohmpilot", map)));
    } else
    {
      resultFields.add(new ResultField("Ohmpilot", count("OhmPilot", map)));
    }

    if (!gen24)
    {
      resultFields.add(new ResultField("SensorCard", map.get("SensorCard")));
      resultFields.add(new ResultField("StringControl", map.get("StringControl")));
      resultFields.add(new ResultField("Inverter", map.get("Inverter")));
      Object inverterId = String.valueOf(map.getOrDefault("Inverter_" + deviceId + "_DT", BigDecimal.ZERO));
      resultFields.add(new ResultField("InverterID", inverterId));
      String product = modelMap.getOrDefault(numericHelper.getInteger(inverterId, 0), "unbekannt");
      resultFields.add(new ResultField("Produkt", product));
    } else
    {
      resultFields.add(new ResultField("Produkt", modelMap.get(1)));
    }
  }

  private void getPowerFlowRealtimeData()
  {
    Logger.debug("getPowerFlowRealtimeData");
    url = String.format("%sGetPowerFlowRealtimeData.fcgi", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    resultFields.add(new ResultField("SummeWattstundenGesamtHeute", map.get("Site_E_Day")));
    resultFields.add(new ResultField("SummeWattstundenGesamtJahr", map.get("Site_E_Year")));
    resultFields.add(new ResultField("SummeWattstundenGesamt", map.get("Site_E_Total")));
    if (map.containsKey("Site_Meter_Location"))
    {
      resultFields.add(new ResultField("Meter_Location", map.get("Site_Meter_Location")));
    } else
    {
      resultFields.add(new ResultField("Meter_Location", map.get("Site_Meter_Location_Current")));
    }
    resultFields.add(new ResultField("Mode", map.get("Site_Mode")));
    resultFields.add(new ResultField("SummePowerGrid", map.get("Site_P_Grid")));
    resultFields.add(new ResultField("SummePowerLoad", map.get("Site_P_Load")));
    resultFields.add(new ResultField("SummePowerAkku", map.get("Site_P_Akku")));
    resultFields.add(new ResultField("SummePowerPV", map.get("Site_P_PV")));
    resultFields.add(new ResultField("Rel_Autonomy", map.get("Site_rel_Autonomy")));
    resultFields.add(new ResultField("Rel_SelfConsumption", map.get("Site_rel_SelfConsumption")));
    resultFields.add(new ResultField("Akkustand_SOC", map.get("Inverters_1_SOC")));

    ResultField wattHeuteField = getValidResultField("WattstundenGesamtHeute");
    ResultField wattJahrField = getValidResultField("WattstundenGesamtJahr");
    if (wattHeuteField != null && wattJahrField != null
        && wattHeuteField.getNumericValue().compareTo(BigDecimal.ZERO) == 0
        && wattJahrField.getNumericValue().compareTo(BigDecimal.ZERO) == 0)
    {
      resultFields.add(new ResultField("WattstundenGesamtHeute", map.get("Site_E_Day")));
      resultFields.add(new ResultField("WattstundenGesamtJahr", map.get("Site_E_Year")));
    }
  }

  private void getMeterRealtimeData()
  {
    Logger.debug("getMeterRealtimeData");
    url = String.format("%sGetMeterRealtimeData.cgi?Scope=System", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    // Channel 1
    if (gen24 && map.containsKey("0_SMARTMETER_POWERACTIVE_MEAN_SUM_F64"))
    {
      resultFields.add(new ResultField("Meter1_Wirkleistung", map.get("0_SMARTMETER_POWERACTIVE_MEAN_SUM_F64")));
      resultFields.add(new ResultField("Meter1_Blindleistung", map.get("0_SMARTMETER_POWERREACTIVE_MEAN_SUM_F64")));
      resultFields.add(new ResultField("Meter1_Scheinleistung", map.get("0_SMARTMETER_POWERAPPARENT_MEAN_SUM_F64")));
      resultFields.add(new ResultField("Meter1_EnergieProduziert", map.get("0_SMARTMETER_ENERGYACTIVE_PRODUCED_SUM_F64")));
      resultFields.add(new ResultField("Meter1_EnergieVerbraucht", map.get("0_SMARTMETER_ENERGYACTIVE_CONSUMED_SUM_F64")));
      resultFields.add(new ResultField("WattstundenGesamt", map.get("0_SMARTMETER_ENERGYACTIVE_PRODUCED_SUM_F64")));
    } else
    {
      resultFields.add(new ResultField("Meter1_Wirkleistung", map.get("0_PowerReal_P_Sum")));
      resultFields.add(new ResultField("Meter1_Blindleistung", map.get("0_PowerReactive_Q_Sum")));
      resultFields.add(new ResultField("Meter1_Scheinleistung", map.get("0_PowerApparent_S_Sum")));
      resultFields.add(new ResultField("Meter1_EnergieProduziert", map.get("0_EnergyReal_WAC_Sum_Produced")));
      resultFields.add(new ResultField("Meter1_EnergieVerbraucht", map.get("0_EnergyReal_WAC_Sum_Consumed")));

    }
    // Channel 2
    resultFields.add(new ResultField("Meter2_Wirkleistung", map.get("1_PowerReal_P_Sum")));
    resultFields.add(new ResultField("Meter2_Blindleistung", map.get("1_PowerReactive_Q_Sum")));
    resultFields.add(new ResultField("Meter2_Scheinleistung", map.get("1_PowerApparent_S_Sum")));
    resultFields.add(new ResultField("Meter2_EnergieProduziert", map.get("1_EnergyReal_WAC_Sum_Produced")));
    resultFields.add(new ResultField("Meter2_EnergieVerbraucht", map.get("1_EnergyReal_WAC_Sum_Consumed")));
    // Channel 3
    resultFields.add(new ResultField("Meter3_Wirkleistung", map.get("2_PowerReal_P_Sum")));
    resultFields.add(new ResultField("Meter3_Blindleistung", map.get("2_PowerReactive_Q_Sum")));
    resultFields.add(new ResultField("Meter3_Scheinleistung", map.get("2_PowerApparent_S_Sum")));
    resultFields.add(new ResultField("Meter3_EnergieProduziert", map.get("2_EnergyReal_WAC_Sum_Produced")));
    resultFields.add(new ResultField("Meter3_EnergieVerbraucht", map.get("2_EnergyReal_WAC_Sum_Consumed")));

  }

  private void getOhmPilotRealtimeData()
  {
    Logger.debug("getOhmPilotRealtimeData");
    url = String.format("%sGetOhmPilotRealtimeData.cgi?Scope=System", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    resultFields.add(new ResultField("Ohmpilot_EnergieGesamt", map.get("0_EnergyReal_WAC_Sum_Consumed")));
    resultFields.add(new ResultField("Ohmpilot_Wirkleistung", map.get("0_PowerReal_PAC_Sum")));
    resultFields.add(new ResultField("Ohmpilot_Temperatur", map.get("0_Temperature_Channel_1")));
  }

  private void getStringRealtimeData()
  {
    Logger.debug("getStringRealtimeData");
    url = String.format("%sGetStringRealtimeData.cgi?Scope=System&DataCollection=NowStringControlData", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
  }

  private void getStorageRealtimeData()
  {
    Logger.debug("getStorageRealtimeData");
    url = String.format("%sGetStorageRealtimeData.cgi?Scope=System", baseUrl);
    Map<String, Object> map = getResultMapFromUrl(url);
    Logger.debug("map: {}", map);
    resultFields.add(new ResultField("Batterie_Max_Kapazitaet", map.get("0_Controller_Capacity_Maximum")));
    resultFields.add(new ResultField("Batterie_Strom_DC", map.get("0_Controller_Current_DC")));
    resultFields.add(new ResultField("Batterie_Hersteller", map.get("0_Controller_Details_Manufacturer")));
    resultFields.add(new ResultField("Batterie_Seriennummer", map.get("0_Controller_Details_Serial")));
    resultFields.add(new ResultField("Batterie_StateOfCharge_Relative", map.get("0_Controller_StateOfCharge_Relative")));
    resultFields.add(new ResultField("Batterie_StatInverter_1_DTus_Batteriezellen", map.get("0_Controller_Status_BatteryCell")));
    resultFields.add(new ResultField("Batterie_Zellentemperatur", map.get("0_Controller_Temperature_Cell")));
    resultFields.add(new ResultField("Batterie_Spannung_DC", map.get("0_Controller_Voltage_DC")));
  }

  private int count(String name, Map<String, Object> map)
  {
    long count = 0L;
    for (Map.Entry<String, Object> entry : map.entrySet())
    {
      if (entry.getKey().startsWith(name))
      {
        count++;
      }
    }
    return (int) count;
  }

  private Map<String, Object> getResultMapFromUrl(String url)
  {
    Map<String, Object> map = jsonTool.getSimpleMapFromUrl(url, null);
    Object status = map.get("Head_Status_Code");
    Object reason = map.getOrDefault("Head_Status_Reason", "unknown");
    int statusValue = numericHelper.getInteger(status, -1);
    // 0 = ok
    if (statusValue != 0)
    {
      Logger.error("couldn't get valid response from url {}, reason is {}", url, reason);
      return Collections.emptyMap();
    }
    return getObjectMap(map);
  }

  // removes leading Body_Data and groups _VALUES_ fields
  private Map<String, Object> getObjectMap(Map<String, Object> map)
  {
    Map<String, Object> result = new HashMap<>();
    Map<String, AbstractMap.SimpleEntry<Integer, Object>> values = new HashMap<>();
    Matcher matcher;
    for (Map.Entry<String, Object> entry : map.entrySet())
    {
      String key = entry.getKey();
      if (key.startsWith(BODY_DATA))
      {
        matcher = pattern.matcher(key);
        if (matcher.find())
        {
          String name = matcher.group(1);
          Integer value = Integer.valueOf(matcher.group(2));
          AbstractMap.SimpleEntry<Integer, Object> pair = values.get(name);
          Integer oldValue = pair != null ? pair.getKey() : null;
          if (oldValue == null || value > oldValue)
          {
            values.put(name, new AbstractMap.SimpleEntry<>(value, entry.getValue()));
          }
        } else
        {
          result.put(key.substring(10), entry.getValue());
        }
      }
    }
    for (Map.Entry<String, AbstractMap.SimpleEntry<Integer, Object>> entry : values.entrySet())
    {
      AbstractMap.SimpleEntry<Integer, Object> pair = entry.getValue();
      result.put(entry.getKey(), pair.getValue());
    }
    return result;
  }

  @Override protected void correctValues()
  {
    ResultField summePowerField = getValidResultField("SummePowerPV");
    BigDecimal solarleistung = BigDecimal.ZERO;
    if (summePowerField != null && summePowerField.getNumericValue().compareTo(BigDecimal.ZERO) > 0)
    {
      solarleistung = summePowerField.getNumericValue();
    } else
    {
      ResultField spannungField = getValidResultField("Solarspannung");
      ResultField stromField = getValidResultField("Solarstrom");
      if (spannungField != null && stromField != null)
      {
        solarleistung = spannungField.getNumericValue().multiply(stromField.getNumericValue());
      }
    }
    resultFields.add(new ResultField("Solarleistung", ResultFieldStatus.VALID, FieldType.NUMBER, solarleistung));

    ResultField summePowerGridField = getValidResultField("SummePowerGrid");
    ResultField summePowerLoadField = getValidResultField("SummePowerLoad");
    if (summePowerGridField != null)
    {
      BigDecimal summePowerGrid = summePowerGridField.getNumericValue();
      BigDecimal summePowerLoad = summePowerLoadField != null ? summePowerLoadField.getNumericValue() : BigDecimal.ZERO;
      BigDecimal einspeisung;
      BigDecimal verbrauch;
      BigDecimal bezug;
      if (summePowerGrid.compareTo(BigDecimal.ZERO) < 0)
      {
        einspeisung = summePowerGrid.abs();
        verbrauch = summePowerLoad.abs();
        bezug = BigDecimal.ZERO;
      } else
      {
        einspeisung = BigDecimal.ZERO;
        bezug = summePowerGrid;
        verbrauch = summePowerLoad.abs();
      }
      resultFields.add(new ResultField("Einspeisung", ResultFieldStatus.VALID, FieldType.NUMBER, einspeisung));
      resultFields.add(new ResultField("Bezug", ResultFieldStatus.VALID, FieldType.NUMBER, bezug));
      resultFields.add(new ResultField("Verbrauch", ResultFieldStatus.VALID, FieldType.NUMBER, verbrauch));
    }

    if (gen24)
    {
      ResultField string1Field = getValidResultField("Solarleistung_String_1");
      ResultField string2Field = getValidResultField("Solarleistung_String_2");
      ResultField string3Field = getValidResultField("Solarleistung_String_3");
      BigDecimal string1 = (string1Field != null) ? string1Field.getNumericValue() : BigDecimal.ZERO;
      BigDecimal string2 = (string2Field != null) ? string2Field.getNumericValue() : BigDecimal.ZERO;
      BigDecimal string3 = (string3Field != null) ? string3Field.getNumericValue() : BigDecimal.ZERO;
      resultFields.add(new ResultField("SolarleistungGen24", ResultFieldStatus.VALID, FieldType.NUMBER, string1.add(string2)
                                                                                                               .add(string3)));
    }

  }

}
