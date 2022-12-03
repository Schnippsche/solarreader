package de.schnippsche.solarreader.backend.fields;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Field definition for a specific device
 */
public class DeviceField implements Comparable<DeviceField>
{
  private String name;
  private String unit;
  private int offset;
  private int count;
  private Integer register;
  private BigDecimal factor;
  private String note;
  private FieldType type;
  private String command;

  public DeviceField()
  {
    name = null;
    unit = null;
    offset = 0;
    count = 0;
    register = null;
    factor = null;
    note = null;
    type = null;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getUnit()
  {
    return unit;
  }

  public void setUnit(String unit)
  {
    this.unit = unit;
  }

  public int getOffset()
  {
    return offset;
  }

  public void setOffset(int offset)
  {
    this.offset = offset;
  }

  public int getCount()
  {
    return count;
  }

  public void setCount(int count)
  {
    this.count = count;
  }

  public Integer getRegister()
  {
    return register;
  }

  public void setRegister(Integer register)
  {
    this.register = register;
  }

  public BigDecimal getFactor()
  {
    return factor;
  }

  public void setFactor(BigDecimal factor)
  {
    this.factor = factor;
  }

  public String getNote()
  {
    return note;
  }

  public void setNote(String note)
  {
    this.note = note;
  }

  public FieldType getType()
  {
    return type;
  }

  public void setType(FieldType type)
  {
    this.type = type;
  }

  public String getCommand()
  {
    return command;
  }

  public void setCommand(String command)
  {
    this.command = command;
  }

  public ResultField createResultField(Object value)
  {
    if (value != null)
    {
      ResultField resultField = new ResultField(this, ResultFieldStatus.VALID, value);
      if (getType() != FieldType.STRING && getType() != FieldType.STRING_LITTLE_ENDIAN && getType() != FieldType.BINARY)
      {
        resultField.setType(FieldType.NUMBER);
      }
      return resultField;
    }
    return new ResultField(this, ResultFieldStatus.READERROR, null);
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
    DeviceField that = (DeviceField) o;
    return name.equals(that.name);
  }

  @Override public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override public String toString()
  {
    return String.format("Field{name='%s', unit='%s', offset=%d, count=%d, register=%d, factor=%s, type='%s', note='%s', command='%s'}", name, unit, offset, count, register, factor, type, note, command);
  }

  @Override public int compareTo(DeviceField other)
  {
    if (this.register < other.register)
    {
      return -1;
    }
    if (this.register > other.register)
    {
      return +1;
    }

    return this.offset - other.offset;
  }

}
