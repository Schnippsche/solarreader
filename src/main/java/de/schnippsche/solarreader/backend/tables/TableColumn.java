package de.schnippsche.solarreader.backend.tables;

import java.util.Objects;

public class TableColumn
{
  private final String name;
  private final Object value;
  private final TableColumnType type;

  public TableColumn(String name, TableColumnType type, Object value)
  {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public String getName()
  {
    return name;
  }

  public TableColumnType getType()
  {
    return type;
  }

  public Object getValue()
  {
    return value;
  }

  public String getTypedValue()
  {
    return (TableColumnType.NUMBER == type) ? value.toString() : "\"" + value + "\"";
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
    TableColumn that = (TableColumn) o;
    return name.equals(that.name);
  }

  @Override public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override public String toString()
  {
    return String.format("TableColumn{name='%s', value=%s}", name, getTypedValue());
  }

}
