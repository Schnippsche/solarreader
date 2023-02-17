package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.MqttField;
import de.schnippsche.solarreader.backend.fields.TableField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Specification
{
  private final HashMap<AdditionalParameter, Object> additionalParameters;
  private String description;
  private List<DeviceField> devicefields;
  private List<TableField> databasefields;
  private List<MqttField> mqttFields;

  public Specification()
  {
    this.description = "";
    this.devicefields = new ArrayList<>();
    this.databasefields = new ArrayList<>();
    this.mqttFields = new ArrayList<>();
    this.additionalParameters = new HashMap<>();
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public List<DeviceField> getDevicefields()
  {
    return devicefields;
  }

  public void setDevicefields(List<DeviceField> devicefields)
  {
    this.devicefields = devicefields;
  }

  public List<TableField> getDatabasefields()
  {
    return databasefields;
  }

  public void setDatabasefields(List<TableField> databasefields)
  {
    this.databasefields = databasefields;
  }

  public List<MqttField> getMqttFields()
  {
    return mqttFields;
  }

  public void setMqttFields(List<MqttField> mqttFields)
  {
    this.mqttFields = mqttFields;
  }

  public String getAllowedCommandsRegexp()
  {
    return getAdditionalParameterAsString(AdditionalParameter.ALLOWED_COMMANDS_REGEXP);
  }

  public Map<AdditionalParameter, Object> getAdditionalParameters()
  {
    return additionalParameters;
  }

  public String getAdditionalParameterAsString(AdditionalParameter parameter)
  {
    Object stringObject = additionalParameters.get(parameter);
    return stringObject != null ? String.valueOf(stringObject) : null;
  }

  public int getAdditionalParameterAsInteger(AdditionalParameter parameter, int defaultValue)
  {
    Object intObject = additionalParameters.get(parameter);
    return new NumericHelper().getInteger(intObject, defaultValue);
  }

  @Override public String toString()
  {
    return String.format("Specification{description='%s', deviceFields=%s, databaseFields=%s, mqttFields=%s}", description, devicefields, databasefields, mqttFields);
  }

}
