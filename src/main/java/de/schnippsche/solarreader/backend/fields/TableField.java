package de.schnippsche.solarreader.backend.fields;

import de.schnippsche.solarreader.backend.tables.TableColumnType;

import java.util.Objects;

public class TableField
{
  private String tablename;
  private String columnname;
  private TableColumnType columntype;
  private TableFieldType sourcetype;
  private String sourcevalue;
  private transient Object value;

  public TableField()
  {
    this.columntype = TableColumnType.STRING;
    this.sourcetype = TableFieldType.RESULTFIELD;
  }

  public boolean isString()
  {
    return (TableColumnType.STRING == this.columntype);
  }

  public TableColumnType getColumntype()
  {
    return columntype;
  }

  public void setColumnType(TableColumnType type)
  {
    this.columntype = type;
  }

  public String getTablename()
  {
    return tablename;
  }

  public void setTablename(String tablename)
  {
    this.tablename = tablename;
  }

  public String getColumnname()
  {
    return columnname;
  }

  public void setColumnname(String columnname)
  {
    this.columnname = columnname;
  }

  public String getValue()
  {
    return isString() ? (String.format("\"%s\"", value)) : String.valueOf(value);
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
    TableField that = (TableField) o;
    return tablename.equals(that.tablename) && columnname.equals(that.columnname);
  }

  @Override public int hashCode()
  {
    return Objects.hash(tablename, columnname);
  }

  @Override public String toString()
  {
    return String.format("TableField{tablename='%s', columnname='%s', columntype='%s',  sourcetype=%s, sourcevalue=%s, value=%s}", tablename, columnname, columntype, sourcetype, sourcevalue, getValue());
  }

}
