package de.schnippsche.solarreader.backend.fields;

import de.schnippsche.solarreader.backend.utils.NumericHelper;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * field after reading from device
 */
public class ResultField
{

  private String name;
  private ResultFieldStatus status;
  private FieldType type;
  private Object value;

  public ResultField(DeviceField deviceField)
  {
    this.name = deviceField.getName();
    this.type = deviceField.getType();
  }

  public ResultField(DeviceField deviceField, ResultFieldStatus resultFieldStatus)
  {
    this(deviceField, resultFieldStatus, null);
  }

  public ResultField(DeviceField deviceField, ResultFieldStatus resultFieldStatus, Object readvalue)
  {
    this.name = deviceField.getName();
    this.type = deviceField.getType();
    setValue(readvalue);
    setStatus(resultFieldStatus);
    if (deviceField.getFactor() != null)
    {
      if (new NumericHelper().isNumericValue(value))
      {
        this.value = getNumericValue().multiply(deviceField.getFactor());
      } else
      {
        status = ResultFieldStatus.INVALIDNUMBER;
      }
    }
  }

  public ResultField(DeviceField deviceField, Object readvalue)
  {
    this(deviceField, readvalue != null ? ResultFieldStatus.VALID : ResultFieldStatus.EMPTY, readvalue);
  }

  public ResultField(String name, ResultFieldStatus resultFieldStatus, Object readvalue)
  {
    this(name, resultFieldStatus, (readvalue instanceof BigDecimal || readvalue instanceof Integer
                                   || readvalue instanceof Long) ? FieldType.NUMBER : FieldType.STRING, readvalue);
  }

  public ResultField(String name, Object readvalue)
  {
    this(name, readvalue != null ? ResultFieldStatus.VALID : ResultFieldStatus.EMPTY, (readvalue instanceof BigDecimal
                                                                                       || readvalue instanceof Integer
                                                                                       || readvalue instanceof Long) ? FieldType.NUMBER : FieldType.STRING, readvalue);
  }

  public ResultField(String name, ResultFieldStatus resultFieldStatus, FieldType fieldType, Object readvalue)
  {
    this.name = name;
    this.type = fieldType;
    setStatus(resultFieldStatus);
    setValue(readvalue);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public ResultFieldStatus getStatus()
  {
    return status;
  }

  public void setStatus(ResultFieldStatus status)
  {
    this.status = status;
  }

  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    if (value == null)
    {
      this.status = ResultFieldStatus.EMPTY;
      this.value = null;
      return;
    }

    this.value = value;
  }

  public BigDecimal getNumericValue()
  {
    if (value == null)
    {
      return BigDecimal.ZERO;
    }
    if (value instanceof BigDecimal)
    {
      return (BigDecimal) value;
    }
    NumericHelper numericHelper = new NumericHelper();
    if (numericHelper.isNumericValue(value))
    {
      return numericHelper.getBigDecimal(value.toString());
    }
    this.status = ResultFieldStatus.INVALIDNUMBER;
    return BigDecimal.ZERO;
  }

  public byte[] getBinaryValue()
  {
    if (getType() != FieldType.BINARY)
    {
      return new byte[0];
    }
    return (byte[]) getValue();
  }

  public String getStringValue()
  {
    return value == null ? "" : String.valueOf(value);
  }

  public boolean isValid()
  {
    return this.status == ResultFieldStatus.VALID;
  }

  public boolean isName(String fieldname)
  {
    return fieldname.equalsIgnoreCase(this.name);
  }

  public FieldType getType()
  {
    return type;
  }

  public void setType(FieldType type)
  {
    this.type = type;
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
    ResultField that = (ResultField) o;
    return name.equals(that.name);
  }

  @Override public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override public String toString()
  {
    String newValue;
    switch (type)
    {
      case BINARY:
        newValue = "0x" + new NumericHelper().byteArrayToHexString((byte[]) value);
        break;
      case STRING:
        newValue = "'" + value + "'";
        break;
      default:
        if (value instanceof BigDecimal)
        {
          newValue = ((BigDecimal) value).toPlainString();
        } else
        {
          newValue = String.valueOf(value);
        }
    }
    return String.format("ResultField{name='%s', status=%s, type='%s', value=%s}", name, status, type, newValue);
  }

}
