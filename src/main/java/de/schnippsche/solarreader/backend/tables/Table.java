package de.schnippsche.solarreader.backend.tables;

import java.util.ArrayList;
import java.util.List;

public class Table
{
  private final String tableName;
  private final List<TableRow> tableRows;

  /**
   * Instantiates a new Table.
   *
   * @param tableName the table name
   */
  public Table(String tableName)
  {
    this.tableName = tableName;
    this.tableRows = new ArrayList<>();
  }

  /**
   * Gets table name.
   *
   * @return the table name
   */
  public String getTableName()
  {
    return this.tableName;
  }

  /**
   * Clear.
   */
  public void clear()
  {
    this.tableRows.clear();
  }

  /**
   * Add table row.
   *
   * @param tableRow the table row
   */
  public void addTableRow(TableRow tableRow)
  {
    this.tableRows.add(tableRow);
  }

  /**
   * Get table row.
   *
   * @param index the index
   * @return the table row
   */
  public TableRow get(int index)
  {
    return this.tableRows.get(index);
  }

  /**
   * Gets table rows.
   *
   * @return the table rows
   */
  public List<TableRow> getTableRows()
  {
    return this.tableRows;
  }

  @Override public String toString()
  {
    return "Table{" + "tableName='" + tableName + '\'' + ", tableRows=" + tableRows + '}';
  }

}
