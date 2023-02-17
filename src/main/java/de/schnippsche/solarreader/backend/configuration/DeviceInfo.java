package de.schnippsche.solarreader.backend.configuration;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class DeviceInfo
{
  private final String uuid;
  private final HashSet<DeviceInfoField> prompts;
  private final HashMap<ConfigDeviceField, String> defaults;
  private String deviceName;
  private String deviceClass;
  private String deviceSpecification;

  public DeviceInfo(String deviceName, String deviceClass)
  {
    this.prompts = new HashSet<>();
    this.defaults = new HashMap<>(); // do not use enum map because converting to json sucks!
    this.uuid = UUID.randomUUID().toString();
    this.deviceName = deviceName;
    this.deviceClass = deviceClass;
    this.deviceSpecification = null;
  }

  public DeviceInfo()
  {
    this("", "");
  }

  public void setUsbStandards()
  {
    defaults.put(ConfigDeviceField.BAUDRATE, "9600");
    defaults.put(ConfigDeviceField.DATABITS, "8");
    defaults.put(ConfigDeviceField.PARITY, "" + AbstractSerialConnection.NO_PARITY);
    defaults.put(ConfigDeviceField.STOPBITS, "" + AbstractSerialConnection.ONE_STOP_BIT);
    defaults.put(ConfigDeviceField.ECHO, "FALSE");
    defaults.put(ConfigDeviceField.OPENDELAY, "" + AbstractSerialConnection.OPEN_DELAY);
    defaults.put(ConfigDeviceField.FLOWCONTROLIN, "" + AbstractSerialConnection.FLOW_CONTROL_DISABLED);
    defaults.put(ConfigDeviceField.FLOWCONTROLOUT, "" + AbstractSerialConnection.FLOW_CONTROL_DISABLED);
    defaults.put(ConfigDeviceField.ENCODING, Modbus.SERIAL_ENCODING_RTU);
  }

  public HashSet<DeviceInfoField> getPrompts()
  {
    return prompts;
  }

  public HashMap<ConfigDeviceField, String> getDefaults()
  {
    return defaults;
  }

  public String getUuid()
  {
    return uuid;
  }

  public String getDeviceName()
  {
    return deviceName;
  }

  public void setDeviceName(String deviceName)
  {
    this.deviceName = deviceName;
  }

  public String getDeviceClass()
  {
    return deviceClass;
  }

  public void setDeviceClass(String deviceClass)
  {
    this.deviceClass = deviceClass;
  }

  public String getDeviceSpecification()
  {
    return deviceSpecification;
  }

  public void setDeviceSpecification(String deviceSpecification)
  {
    this.deviceSpecification = deviceSpecification;
  }

}
