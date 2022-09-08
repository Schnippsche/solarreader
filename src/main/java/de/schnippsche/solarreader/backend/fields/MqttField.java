package de.schnippsche.solarreader.backend.fields;

import java.util.Objects;

public class MqttField
{
  private String name;
  private TableFieldType sourcetype;
  private String sourcevalue;
  private transient Object value;

  public MqttField()
  {
    this.sourcetype = TableFieldType.RESULTFIELD;
  }

  public MqttField(String name, TableFieldType sourcetype, String sourcevalue, Object value)
  {
    this.name = name;
    this.sourcetype = sourcetype;
    this.sourcevalue = sourcevalue;
    this.value = value;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  public TableFieldType getSourcetype()
  {
    return sourcetype;
  }

  public void setSourcetype(TableFieldType sourcetype)
  {
    this.sourcetype = sourcetype;
  }

  public String getSourcevalue()
  {
    return sourcevalue;
  }

  public void setSourcevalue(String sourcevalue)
  {
    this.sourcevalue = sourcevalue;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    MqttField that = (MqttField) o;
    return name.equals(that.name);
  }

  @Override public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override public String toString()
  {
    return String.format("MqttField{name='%s', sourcetype=%s, sourcevalue=%s, value=%s}", name, sourcetype, sourcevalue, value);
  }

}
