package de.schnippsche.solarreader.backend.serializes.shelly;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class ShellyWrapper
{
  @SerializedName("lights")
  public List<Light> lights = null;
  @SerializedName("relays")
  public List<Relay> relays = null;
  @SerializedName("wifi_sta")
  public WifiSta wifiSta;
  @SerializedName("cloud")
  public Cloud cloud;
  @SerializedName("mqtt")
  public Mqtt mqtt;
  @SerializedName("time")
  public String time;
  @SerializedName("timestamp")
  public Integer timestamp;
  @SerializedName("unixtime")
  public Integer unixtime;
  @SerializedName("serial")
  public Integer serial;
  @SerializedName("has_update")
  public Boolean hasUpdate;
  @SerializedName("mac")
  public String mac;
  @SerializedName("cfg_changed_cnt")
  public Integer cfgChangedCnt;
  @SerializedName("actions_stats")
  public ActionsStats actionsStats;
  @SerializedName("rollers")
  public List<Roller> rollers = null;
  @SerializedName("meters")
  public List<Meter> meters = null;
  @SerializedName("inputs")
  public List<Input> inputs = null;
  @SerializedName("temperature")
  public BigDecimal temperature;
  @SerializedName("overtemperature")
  public Boolean overtemperature;
  @SerializedName("tmp")
  public Tmp tmp;
  @SerializedName("hum")
  public Hum hum;
  @SerializedName("lux")
  public Lux lux;
  @SerializedName("bat")
  public Bat bat;
  @SerializedName("temperature_status")
  public String temperatureStatus;
  @SerializedName("update")
  public Update update;
  @SerializedName("ram_total")
  public Integer ramTotal;
  @SerializedName("ram_free")
  public Integer ramFree;
  @SerializedName("fs_size")
  public Integer fsSize;
  @SerializedName("fs_free")
  public Integer fsFree;
  @SerializedName("voltage")
  public BigDecimal voltage;
  @SerializedName("uptime")
  public Integer uptime;
  @SerializedName("motion")
  public Integer motion;
  @SerializedName("charger")
  public Integer charger;
  @SerializedName("emeters")
  public List<Emeter> emeters = null;
  @SerializedName("accel")
  public Accel accel;
  @SerializedName("sensor")
  public Sensor sensor;
  @SerializedName("act_reasons")
  public List<String> actReasons = null;

}
