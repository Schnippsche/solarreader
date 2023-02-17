package de.schnippsche.solarreader.backend.tables;

import java.util.ArrayList;
import java.util.List;

public class TableRow
{
  private final List<TableColumn> columns;

  public TableRow()
  {
    this.columns = new ArrayList<>();
  }

  public void putColumn(String columnName, TableColumnType type, Object value)
  {
    this.putColumn(new TableColumn(columnName, type, value));
  }

  public void putColumn(TableColumn column)
  {
    // Search for duplicate and replace if found
    int position = columns.indexOf(column);
    if (position == -1)
    {
      this.columns.add(column);
    } else
    {
      this.columns.set(position, column);
    }
  }

  public List<TableColumn> getColumns()
  {
    return columns;
  }

  public void clear()
  {
    this.columns.clear();
  }

  @Override public String toString()
  {
    return "TableRow{" + "columns=" + columns + '}';
  }

}
