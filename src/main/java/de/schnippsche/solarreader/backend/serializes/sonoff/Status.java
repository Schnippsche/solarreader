package de.schnippsche.solarreader.backend.serializes.sonoff;

import com.google.gson.annotations.SerializedName;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.serializes.openweather.ResultFieldConverter;

import java.util.List;

public class Status implements ResultFieldConverter
{
  protected static final String[] MODULE_NAMES =
    {"Template", "Sonoff Basic", "Sonoff RF", "Sonoff SV", "Sonoff TH", "Sonoff Dual", "Sonoff POW", "Sonoff 4Ch", "Sonoff S2X", "Slampher", "Sonoff Touch", "Sonoff LED", "1 Channel", "4 Channel", "Motor C/AC", "ElectroDragon", "EXS Relay(s)", "WION", "Generic", "Sonoff Dev", "H801", "Sonoff SC", "Sonoff BN-SZ", "Sonoff 4Ch Pro", "Huafan SS", "Sonoff Bridge", "Sonoff B1", "Ailight", "Sonoff T1 1Ch", "Sonoff T1 2Ch", "Sonoff T1 3Ch", "Supla Espablo", "Witty Cloud", "Yunshan Relay", "MagicHome", "Luani HVIO", "KMC 70011", "Arilux LC01", "Arilux LC11", "Sonoff Dual R2", "Arilux LC06", "Sonoff S31", "Zengge WF017", "Sonoff Pow R2", "Sonoff IFan02", "Blitzwolf SHP", "Shelly 1", "Shelly 2", "Xiaomi Philips", "Neo Coolcam", "ESP SwitCh", "Obi Socket", "Teckin", "APLIC WDP303075", "TuyaMCU", "Gosund SP1 v23", "Armtronix Dimmers", "SK03 Outdoor (Tuya)", "PS-16-DZ", "Teckin US", "Manzoku Strip (EU 4)", "Obi Socket 2", "YTF IR Bridge", "Digoo DG-SP202", "KA10", "Luminea ZX2820", "Mi Desk Lamp", "SP10", "WAGA CHCZ02MB", "SYF05", "Sonoff L1", "Sonoff iFan03", "EX-Store Dimmer", "PWM Dimmer", "Sonoff D1 Dimmer", "Sonoff ZBBridge"};

  @SerializedName("Module")
  public Integer module;

  @SerializedName("DeviceName")
  public String deviceName;

  @SerializedName("FriendlyName")
  public List<String> friendlyName = null;

  @SerializedName("Topic")
  public String topic;

  @SerializedName("ButtonTopic")
  public String buttonTopic;

  @SerializedName("Power")
  public Integer power;

  @SerializedName("PowerOnState")
  public Integer powerOnState;

  @SerializedName("LedState")
  public Integer ledState;

  @SerializedName("LedMask")
  public String ledMask;

  @SerializedName("SaveData")
  public Integer saveData;

  @SerializedName("SaveState")
  public Integer saveState;

  @SerializedName("SwitchTopic")
  public String switchTopic;

  @SerializedName("SwitchMode")
  public List<Integer> switchMode = null;

  @SerializedName("ButtonRetain")
  public Integer buttonRetain;

  @SerializedName("SwitchRetain")
  public Integer switchRetain;

  @SerializedName("SensorRetain")
  public Integer sensorRetain;

  @SerializedName("PowerRetain")
  public Integer powerRetain;

  @SerializedName("InfoRetain")
  public Integer infoRetain;

  @SerializedName("StateRetain")
  public Integer stateRetain;

  @Override public ResultField getResultField(DeviceField deviceField)
  {
    switch (deviceField.getName())
    {
      case "status_Module":
        return new ResultField(deviceField, module);
      case "status_ModuleName":
        return new ResultField(deviceField, getModuleName());
      case "status_DeviceName":
        return new ResultField(deviceField, deviceName);
      case "status_FriendlyName":
        // Convert List into one String
        if (friendlyName != null && !friendlyName.isEmpty())
        {
          return new ResultField(deviceField, String.join(" ", friendlyName).trim());
        }
        return null;
      case "status_Topic":
        return new ResultField(deviceField, topic);
      case "status_ButtonTopic":
        return new ResultField(deviceField, buttonTopic);
      case "status_PowerOnState":
        return new ResultField(deviceField, powerOnState);
      case "status_LedState":
        return new ResultField(deviceField, ledState);
      case "status_LedMask":
        return new ResultField(deviceField, ledMask);
      case "status_SwitchTopic":
        return new ResultField(deviceField, switchTopic);
      case "status_SaveData":
        return new ResultField(deviceField, saveData);
      case "status_SaveState":
        return new ResultField(deviceField, saveState);
      case "status_ButtonRetain":
        return new ResultField(deviceField, buttonRetain);
      case "status_SwitchRetain":
        return new ResultField(deviceField, switchRetain);
      case "status_SensorRetain":
        return new ResultField(deviceField, sensorRetain);
      case "status_PowerRetain":
        return new ResultField(deviceField, powerRetain);
      case "status_InfoRetain":
        return new ResultField(deviceField, infoRetain);
      case "status_stateRetain":
        return new ResultField(deviceField, stateRetain);
      default:
        return null;
    }
  }

  private String getModuleName()
  {
    if (module == null)
    {
      return null;
    }
    return module >= 0 && module < MODULE_NAMES.length ? MODULE_NAMES[module] : "unknown Module " + module;
  }

}
